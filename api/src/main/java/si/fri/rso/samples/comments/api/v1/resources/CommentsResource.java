package si.fri.rso.samples.comments.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Counted;
import si.fri.rso.samples.comments.lib.Song;
import si.fri.rso.samples.comments.services.SongsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Log
@ApplicationScoped
@Path("/songs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommentsResource {

    @Inject
    private SongsBean songsBean;

    @GET
    @Counted
    public Response getComments(@QueryParam("imageId") Integer imageId) {

        List<Song> songs;

        if (imageId != null) {
            songs = songsBean.getCommentsForImage(imageId);
        } else {
            songs = songsBean.getSongs();
        }

        return Response.ok(songs).build();
    }

    @GET
    @Path("count")
    public Response getCommentsCount(@QueryParam("imageId") Integer imageId) {

        List<Song> songs;

        if (imageId != null) {
            songs = songsBean.getCommentsForImage(imageId);
        } else {
            songs = songsBean.getSongs();
        }

        return Response.ok(songs.size()).build();
    }

}
