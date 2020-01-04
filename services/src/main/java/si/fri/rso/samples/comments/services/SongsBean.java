package si.fri.rso.samples.comments.services;

import si.fri.rso.samples.comments.lib.Song;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class SongsBean {

    private Logger log = Logger.getLogger(SongsBean.class.getName());

    private List<Song> songs;

    /*
    Integer songId, String authorId, String text, String album, Integer length


     */

    @PostConstruct
    private void init() {
        songs = new ArrayList<>();
        //songs.add(new Song(1, UUID.randomUUID().toString(), "Very nice photo!"));
        //songs.add(new Song(1, songs.get(0).getAuthorId(), "Keep up the good work!"));
        //songs.add(new Song(1, UUID.randomUUID().toString(), "Beautiful"));
        songs.add(new Song(1, UUID.randomUUID().toString(), "Rolling in the Deep", UUID.randomUUID().toString(), 123));
        songs.add(new Song(2, UUID.randomUUID().toString(), "Me!", UUID.randomUUID().toString(), 123));
        songs.add(new Song(3, UUID.randomUUID().toString(), "Horizon", UUID.randomUUID().toString(), 123));
    }

    public List<Song> getSongs() {

        return songs;

    }

    public List<Song> getCommentsForImage(Integer imageId) {

        return songs.stream().filter(song -> song.getSongId().equals(imageId)).collect(Collectors.toList());
    }

}
