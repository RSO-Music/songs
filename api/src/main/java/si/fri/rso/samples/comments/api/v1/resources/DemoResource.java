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
                .add("clani", Json.createArrayBuilder().add("gk7880"))
                .add("opis_projekta", "Moj projekt implementira aplikacijo za nalaganje, pregledovanje ter poslusanje glasbe")
                .add("mikrostoritve", Json.createArrayBuilder().add("http://songs-5-rso-music-2.apps.us-east-1.starter.openshift-online.com/v1/songs"))
                .add("github", Json.createArrayBuilder().add("https://github.com/RSO-Music/songs"))
                .add("travis", Json.createArrayBuilder().add("https://travis-ci.org/RSO-Music/songs"))
                .add("dockerhub", Json.createArrayBuilder().add("https://hub.docker.com/r/3978hge3gu/songs"))
                .build();


        return Response.ok(json.toString()).build();
    }

}
