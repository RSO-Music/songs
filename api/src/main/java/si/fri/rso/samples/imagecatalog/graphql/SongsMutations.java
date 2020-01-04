package si.fri.rso.samples.imagecatalog.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import si.fri.rso.samples.imagecatalog.lib.Songs;
import si.fri.rso.samples.imagecatalog.services.beans.SongsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@GraphQLClass
@ApplicationScoped
public class SongsMutations {

    @Inject
    private SongsBean songsBean;

    @GraphQLMutation
    public Songs addSongs(@GraphQLArgument(name = "songs") Songs songs) {
        songsBean.createSongs(songs);
        return songs;
    }

    @GraphQLMutation
    public DeleteResponse deleteSongs(@GraphQLArgument(name = "id") Integer id) {
        return new DeleteResponse(songsBean.deleteSongs(id));
    }

}
