package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "songs")
@NamedQueries(value =
        {
                @NamedQuery(name = "SongsEntity.getAll",
                        query = "SELECT im FROM SongsEntity im")
        })
public class SongsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "AuthorId")
    private String AuthorId;

    @Column(name = "SongName")
    private String SongName;

    @Column(name = "SongLength")
    private String SongLength;

    @Column(name = "AlbumId")
    private String AlbumId;

    @Column(name = "UploadedAt")
    private Instant UploadedAt;

    @Column(name = "uri")
    private String uri;

    public Integer getSongId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthorId() {
        return AuthorId;
    }

    public void setAuthorId(String AuthorId) {
        this.AuthorId = AuthorId;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String SongName) {
        this.SongName = SongName;
    }

    public String getSongLength() {
        return SongLength;
    }

    public void setSongLength(String SongLength) {
        this.SongLength = SongLength;
    }

    public String getAlbumId() {
        return AlbumId;
    }

    public void setAlbumId(String AlbumId) {
        this.AlbumId = AlbumId;
    }

    public Instant getUploadedAt() {
        return UploadedAt;
    }

    public void setUploadedAt(Instant UploadedAt) {
        this.UploadedAt = UploadedAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}