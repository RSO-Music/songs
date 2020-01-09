package si.fri.rso.samples.imagecatalog.api.v1;

import com.kumuluz.ee.discovery.annotations.RegisterService;
import com.kumuluz.ee.cors.annotations.CrossOrigin;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@RegisterService
@ApplicationPath("/v1")
@CrossOrigin(allowOrigin = "*", allowSubdomains = true, supportedHeaders = "*")
public class SongsApplication extends Application {
}
