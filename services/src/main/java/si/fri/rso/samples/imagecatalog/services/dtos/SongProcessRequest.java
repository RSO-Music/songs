package si.fri.rso.samples.imagecatalog.services.dtos;

public class SongProcessRequest {

    public SongProcessRequest() {
    }

    public SongProcessRequest(String imageId, String imageLocation) {
        this.imageId = imageId;
        this.imageLocation = imageLocation;
    }

    private String imageId;
    private String imageLocation;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
