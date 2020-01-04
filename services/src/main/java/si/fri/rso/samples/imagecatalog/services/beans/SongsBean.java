package si.fri.rso.samples.imagecatalog.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.samples.imagecatalog.lib.Songs;
import si.fri.rso.samples.imagecatalog.models.converters.SongsConverter;
import si.fri.rso.samples.imagecatalog.models.entities.SongsEntity;
import si.fri.rso.samples.imagecatalog.services.config.IntegrationProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RequestScoped
public class SongsBean {

    private Logger log = Logger.getLogger(SongsBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    @Inject
    @DiscoverService("comments-service")
    private Optional<String> baseUrl;

    @Inject
    private IntegrationProperties integrationProperties;

    @Inject
    private SongsBean songsBeanProxy;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
//        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Songs> getSongs() {

        TypedQuery<SongsEntity> query = em.createNamedQuery(
                "SongsEntity.getAll", SongsEntity.class);

        List<SongsEntity> resultList = query.getResultList();

        return resultList.stream().map(SongsConverter::toDto).collect(Collectors.toList());

    }

    public List<Songs> getSongsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, SongsEntity.class, queryParameters).stream()
                .map(SongsConverter::toDto).collect(Collectors.toList());
    }

    public Songs getSongs(Integer id) {

        SongsEntity songsEntity = em.find(SongsEntity.class, id);

        if (songsEntity == null) {
            throw new NotFoundException();
        }

        Songs songs = SongsConverter.toDto(songsEntity);

        if (integrationProperties.isIntegrateWithCommentsService()) {
            songs.setNumberOfComments(songsBeanProxy.getCommentCount(id));
        }

        return songs;
    }

    public Songs createSongs(Songs songs) {

        SongsEntity songsEntity = SongsConverter.toEntity(songs);

        try {
            beginTx();
            em.persist(songsEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (songsEntity.getSongId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return SongsConverter.toDto(songsEntity);
    }

    public Songs putImageMetadata(Integer id, Songs songs) {

        SongsEntity c = em.find(SongsEntity.class, id);

        if (c == null) {
            return null;
        }

        SongsEntity updatedSongsEntity = SongsConverter.toEntity(songs);

        try {
            beginTx();
            updatedSongsEntity.setId(c.getSongId());
            updatedSongsEntity = em.merge(updatedSongsEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return SongsConverter.toDto(updatedSongsEntity);
    }

    public boolean deleteSongs(Integer id) {

        SongsEntity songMetadata = em.find(SongsEntity.class, id);

        if (songMetadata != null) {
            try {
                beginTx();
                em.remove(songMetadata);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }


    //    @Retry
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getCommentCountFallback")
    public Integer getCommentCount(Integer songId) {

        if (baseUrl.isPresent()) {

            log.info("Calling comments service: getting comment count.");

            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/comments/count")
                        .queryParam("songId", songId)
                        .request().get(new GenericType<Integer>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;

    }

    public Integer getCommentCountFallback(Integer songId) {
        return null;
    }


    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
