package si.fri.rso.samples.imagecatalog.api.v1.resources;

//import com.amazonaws.services.s3.AmazonS3Client;

import com.google.gson.Gson;
import com.kumuluz.ee.cors.annotations.CrossOrigin;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import si.fri.rso.samples.imagecatalog.api.v1.dtos.UploadSongResponse;
import si.fri.rso.samples.imagecatalog.lib.Songs;
import si.fri.rso.samples.imagecatalog.services.beans.SongsBean;
import si.fri.rso.samples.imagecatalog.services.clients.AmazonRekognitionClient;
import si.fri.rso.samples.imagecatalog.services.clients.AmazonS3Client;
import si.fri.rso.samples.imagecatalog.services.clients.SongsProcessingApi;
import si.fri.rso.samples.imagecatalog.services.dtos.SongProcessRequest;
import si.fri.rso.samples.imagecatalog.services.streaming.EventProducerImpl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;


@ApplicationScoped
@Path("/songs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(allowOrigin = "*", allowSubdomains = true, supportedHeaders = "*")
public class SongsResource {

    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private Logger log = Logger.getLogger(SongsResource.class.getName());

    final int chunk_size = 1024 * 1024; // 1MB chunks
    //private File audio;

    @Inject
    private SongsBean songsBean;

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    @Inject
    private AmazonS3Client amazonS3Client;

    @Inject
    private EventProducerImpl eventProducer;

    @Inject
    @RestClient
    private SongsProcessingApi songsProcessingApi;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Timed
    public Response getImageMetadata() {

        List<Songs> songsMetadata = songsBean.getSongsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(songsMetadata).build();
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

//    @POST
//    public Response uploadFileMetadata(String body) {
//        Gson gson = new Gson();
//        Songs songs = gson.fromJson(body, NewFileMetadataDTO.class);
//
//        if (newFileMetadata.getFileLabels() != null) {
//            System.out.println(Arrays.toString(newFileMetadata.getFileLabels().toArray()));
//        }
//
//        if (newFileMetadata.getChannelId() == null || newFileMetadata.getFileName() == null ||
//                newFileMetadata.getFilePath() == null || newFileMetadata.getFileType() == null ||newFileMetadata.getUserId() == null) {
//            return Response.status(400).entity(this.responseError(400, "channelId, fileName, filePath, fileType or userId is missing")).build();
//        }
//
//        FileDTO newFile = catalogFileBean.createFileMetadata(newFileMetadata);
//        if (newFile == null) {
//            return Response.status(500).entity(this.responseError(500, "error when writing file metadata to DB")).build();
//        }
//
//        return Response.status(200).entity(this.responseOk("", newFile)).build();
//    }


//    @POST
//    public Response createSongMetadata(Songs songs) {
//
//        if ((songs.getAuthorId() == null || songs.getSongName() == null || songs.getUri() == null)) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        } else {
//            songs = songsBean.createSongs(songs);
//        }
//
//        return Response.status(Response.Status.CONFLICT).entity(songs).build();
//
//    }

    @POST
    public Response createSongMetadata(String body) {
        Gson gson = new Gson();
        Songs songs = gson.fromJson(body, Songs.class);

        System.out.println(body);
        System.out.println(songs);

//        if (songs.getFileLabels() != null) {
//            System.out.println(Arrays.toString(newFcrileMetadata.getFileLabels().toArray()));
//        }
//
//        if (songs.getChannelId() == null || songs.getFileName() == null ||
//                songs.getFilePath() == null || songs.getFileType() == null ||songs.getUserId() == null) {
//            return Response.status(400).entity(this.responseError(400, "channelId, fileName, filePath, fileType or userId is missing")).build();
//        }

//        FileDTO newFile = catalogFileBean.createFileMetadata(newFileMetadata);
//        if (newFile == null) {
//            return Response.status(500).entity(this.responseError(500, "error when writing file metadata to DB")).build();
//        }
//
//        return Response.status(200).entity(this.responseOk("", newFile)).build();

        if ((songs.getAuthorId() == null || songs.getSongName() == null || songs.getUri() == null || songs.getAlbumId() == null || songs.getSongLength() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            songs = songsBean.createSongs(songs);
        }

        return Response.status(Response.Status.CONFLICT).entity(songs + " " + body).build();

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

    @GET
    @Path("/test")
    public Response test(String imageMetadataId) {

        UploadSongResponse uploadSongResponse = new UploadSongResponse();
        uploadSongResponse.setMessage("test successful");
        return Response.status(Response.Status.CREATED).entity(uploadSongResponse).build();
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
    @CrossOrigin(allowOrigin = "*", allowSubdomains = true, supportedHeaders = "*")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)

    public Response uploadSong(InputStream uploadedInputStream) {

        String songId = UUID.randomUUID().toString();
        String songLocation = UUID.randomUUID().toString();

        byte[] bytes = new byte[0];
        try (uploadedInputStream) {
            bytes = uploadedInputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileSha = "";

        try {
            fileSha = SHAsum(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        songId = fileSha;

        UploadSongResponse uploadSongResponse = new UploadSongResponse();

        String uploadedFileUrl = amazonS3Client.uploadFile(bytes, songId);

//        Integer numberOfFaces = amazonRekognitionClient.countFaces(bytes);
//        uploadSongResponse.setNumberOfFaces(numberOfFaces);
//
//        if (numberOfFaces != 1) {pos
//            uploadSongResponse.setMessage("Image must contain one face.");
//            return Response.status(Response.Status.BAD_REQUEST).entity(uploadSongResponse).build();
//
//        }
//
//        List<String> detectedCelebrities = amazonRekognitionClient.checkForCelebrities(bytes);
//
//        if (!detectedCelebrities.isEmpty()) {
//            uploadSongResponse.setMessage("Image must not contain celebrities. Detected celebrities: "
//                    + detectedCelebrities.stream().collect(Collectors.joining(", ")));
//            return Response.status(Response.Status.BAD_REQUEST).entity(uploadSongResponse).build();
//        }

        uploadSongResponse.setMessage("Success." + "songId " + songId + "songLocation " + songLocation + "uploadedFileUrl " + uploadedFileUrl);
        //uploadSongResponse.addHeader("Access-Control-Allow-Origin", "*");
        // Upload image to storage

        // Generate event for image processing
//        eventProducer.produceMessage(songId, songLocation);

        // start image processing over async API
        CompletionStage<String> stringCompletionStage =
                songsProcessingApi.processSongAsynch(new SongProcessRequest(songId, songLocation));

        stringCompletionStage.whenComplete((s, throwable) -> System.out.println(s));
        stringCompletionStage.exceptionally(throwable -> {
            log.severe(throwable.getMessage());
            return throwable.getMessage();
        });

        return Response.status(Response.Status.CREATED).entity(uploadSongResponse).build();
    }

//        public MediaResource() {
//            // serve media from file system
//            String MEDIA_FILE = "testdata/music/Gryffin & Seven Lions - Need Your Love feat. Noah Kahan.mp3";
//            URL url = this.getClass().getResource(MEDIA_FILE);
//            audio = new File(url.getFile());
//        }

    //A simple way to verify if the server supports range headers.
//        @HEAD
//        @Produces("audio/mp3")
//        public Response header() {
//            return Response.ok().status(206).header(HttpHeaders.CONTENT_LENGTH, audio.length()).build();
//        }

    @GET
    @Path("/listen")
    @Produces("audio/mp3")
    public Response streamAudio(@HeaderParam("Range") String range) throws Exception {
        // serve media from file system
        String URLString = "https://rso-music.s3.amazonaws.com/Gryffin+%26+Seven+Lions+-+Need+Your+Love+feat.+Noah+Kahan.mp3";

        URL urlB = new URL(URLString);
        FileUtils.copyURLToFile(urlB, new File("./temp.mp3"));
        System.out.println("before audio");

        String MEDIA_FILE = "./temp.mp3";

        URL url = this.getClass().getResource(MEDIA_FILE);
        System.out.println(url + "url");
        //System.out.println

        File audio = new File(url.getFile());
        System.out.println("after audio");
        return buildStream(audio, range);
    }

    /**
     * Adapted from http://stackoverflow.com/questions/12768812/video-streaming-to-ipad-does-not-work-with-tapestry5/12829541#12829541
     *
     * @param asset Media file
     * @param range range header
     * @return Streaming output
     * @throws Exception IOException if an error occurs in streaming.
     */
    private Response buildStream(final File asset, final String range) throws Exception {
        // range not requested : Firefox does not send range headers
        System.out.println("inside buildStream");
        if (range == null) {
            StreamingOutput streamer = output -> {
                try (FileChannel inputChannel = new FileInputStream(asset).getChannel(); WritableByteChannel outputChannel = Channels.newChannel(output)) {
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                }
            };
            return Response.ok(streamer).status(200).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
        }

        String[] ranges = range.split("=")[1].split("-");
        final int from = Integer.parseInt(ranges[0]);

        /*
          Chunk media if the range upper bound is unspecified. Chrome, Opera sends "bytes=0-"
         */
        int to = 1024 * 1024 + from;
        if (to >= asset.length()) {
            to = (int) (asset.length() - 1);
        }
        if (ranges.length == 2) {
            to = Integer.parseInt(ranges[1]);
        }

        final String responseRange = String.format("cbytes %d-%d/%d", from, to, asset.length());
        final RandomAccessFile raf = new RandomAccessFile(asset, "r");
        raf.seek(from);

        final int len = to - from + 1;
        final MediaStreamer streamer = new MediaStreamer(len, raf);
        Response.ResponseBuilder res = Response.ok(streamer)
                .status(Response.Status.PARTIAL_CONTENT)
                .header("Accept-Ranges", "bytes")
                .header("Content-Range", responseRange)
                .header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth())
                .header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
        return res.build();
    }
}
