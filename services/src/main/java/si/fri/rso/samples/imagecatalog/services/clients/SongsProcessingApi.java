package si.fri.rso.samples.imagecatalog.services.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import si.fri.rso.samples.imagecatalog.services.dtos.SongProcessRequest;

import javax.enterprise.context.Dependent;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.CompletionStage;

@Path("process")
@RegisterRestClient(configKey="image-processing-api")
@Dependent
public interface SongsProcessingApi {

    @POST
    CompletionStage<String> processSongAsynch(SongProcessRequest songProcessRequest);

}
