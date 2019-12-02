package si.fri.rso.samples.comments.api.v1.resources;

import com.kumuluz.ee.common.runtime.EeRuntime;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("demo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DemoResource {

    private Logger log = Logger.getLogger(DemoResource.class.getName());

    @GET
    @Path("instanceid")
    public Response getInstanceId() {

        String instanceId =
                "{\"instanceId\" : \"" + EeRuntime.getInstance().getInstanceId() + "\"}";

        return Response.ok(instanceId).build();
    }

    @GET
    @Path("info")
    public Response info() {

        JsonObject json = Json.createObjectBuilder()
                .add("clani", Json.createArrayBuilder().add("jm1234"))
                .add("opis_projekta", "Nas projekt implementira aplikacijo za upravljanje slik.")
                .add("mikrostoritve", Json.createArrayBuilder().add("http://35.246.130.125:8080/v1/images"))
                .add("github", Json.createArrayBuilder().add("https://github.com/jmezna/rso-image-catalog"))
                .add("travis", Json.createArrayBuilder().add("https://travis-ci.org/jmezna/rso-image-catalog"))
                .add("dockerhub", Json.createArrayBuilder().add("https://hub.docker.com/r/jmezna/rso-image-catalog"))
                .build();


        return Response.ok(json.toString()).build();
    }

}
