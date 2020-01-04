package si.fri.rso.samples.comments.lib;

import java.time.Instant;

public class Song {

    private Integer songId;
    private Instant uploadedAt;
    private String authorId;
    private String name;
    private String albumId;
    private Integer length;

    public Song(Integer songId, String authorId, String name, String albumId, Integer length) {
        this.songId = songId;
        this.uploadedAt = Instant.now();
        this.authorId = authorId;
        this.name = name;
        this.albumId = albumId;
        this.length = length;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }


}
