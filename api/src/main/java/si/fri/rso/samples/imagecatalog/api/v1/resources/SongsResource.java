package si.fri.rso.samples.imagecatalog.api.v1.resources;

import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import si.fri.rso.samples.imagecatalog.api.v1.dtos.UploadImageResponse;
import si.fri.rso.samples.imagecatalog.lib.Songs;
import si.fri.rso.samples.imagecatalog.services.beans.SongsBean;
import si.fri.rso.samples.imagecatalog.services.clients.AmazonRekognitionClient;
import si.fri.rso.samples.imagecatalog.services.clients.SongProcessingApi;
import si.fri.rso.samples.imagecatalog.services.dtos.ImageProcessRequest;
import si.fri.rso.samples.imagecatalog.services.streaming.EventProducerImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SongsResource {

    private Logger log = Logger.getLogger(SongsResource.class.getName());

    @Inject
    private SongsBean songsBean;

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    @Inject
    private EventProducerImpl eventProducer;

    @Inject
    @RestClient
    private SongProcessingApi songProcessingApi;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Timed
    public Response getImageMetadata() {

        List<Songs> imageMetadata = songsBean.getSongsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata).build();
    }

    @GET
    @Path("/{imageMetadataId}")
    public Response getImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId) {

        Songs songs = songsBean.getSongs(imageMetadataId);

        if (songs == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(songs).build();
    }

    @POST
    public Response createImageMetadata(Songs songs) {

        if ((songs.getAuthorId() == null || songs.getSongName() == null || songs.getUri() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            songs = songsBean.createSongs(songs);
        }

        return Response.status(Response.Status.CONFLICT).entity(songs).build();

    }

    @PUT
    @Path("{imageMetadataId}")
    public Response putImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId,
                                     Songs songs) {

        songs = songsBean.putImageMetadata(imageMetadataId, songs);

        if (songs == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_MODIFIED).build();

    }

    @DELETE
    @Path("{imageMetadataId}")
    public Response deleteImageMetadata(@PathParam("imageMetadataId") Integer imageMetadataId) {

        boolean deleted = songsBean.deleteSongs(imageMetadataId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadImage(InputStream uploadedInputStream) {

        String imageId = UUID.randomUUID().toString();
        String imageLocation = UUID.randomUUID().toString();

        byte[] bytes = new byte[0];
        try (uploadedInputStream) {
            bytes = uploadedInputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UploadImageResponse uploadImageResponse = new UploadImageResponse();

        Integer numberOfFaces = amazonRekognitionClient.countFaces(bytes);
        uploadImageResponse.setNumberOfFaces(numberOfFaces);

        if (numberOfFaces != 1) {
            uploadImageResponse.setMessage("Image must contain one face.");
            return Response.status(Response.Status.BAD_REQUEST).entity(uploadImageResponse).build();

        }

        List<String> detectedCelebrities = amazonRekognitionClient.checkForCelebrities(bytes);

        if (!detectedCelebrities.isEmpty()) {
            uploadImageResponse.setMessage("Image must not contain celebrities. Detected celebrities: "
                    + detectedCelebrities.stream().collect(Collectors.joining(", ")));
            return Response.status(Response.Status.BAD_REQUEST).entity(uploadImageResponse).build();
        }

        uploadImageResponse.setMessage("Success.");

        // Upload image to storage

        // Generate event for image processing
//        eventProducer.produceMessage(imageId, imageLocation);

        // start image processing over async API
        CompletionStage<String> stringCompletionStage =
                songProcessingApi.processImageAsynch(new ImageProcessRequest(imageId, imageLocation));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println(s));
        stringCompletionStage.exceptionally(throwable -> {
            log.severe(throwable.getMessage());
            return throwable.getMessage();
        });

        return Response.status(Response.Status.CREATED).entity(uploadImageResponse).build();
    }

}
