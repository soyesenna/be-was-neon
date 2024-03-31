package feed;

import model.User;
import utils.ContentType;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    private String uploaderName;
    private String contents;
    private String imagePath;
    private ContentType imageType;
    private List<Comment> comments = new ArrayList<>();

    public Feed(String uploader, String imagePath, ContentType imageType) {
        this.uploaderName = uploader;
        this.imagePath = imagePath;
        this.contents = "";
        this.imageType = imageType;
    }
    public Feed(String uploader, String imagePath, ContentType imageType,  String contents) {
        this.uploaderName = uploader;
        this.contents = contents;
        this.imagePath = imagePath;
        this.imageType = imageType;
    }

    public String getUploaderName() {
        return this.uploaderName;
    }

    public String getImagePath() {
        return this.imagePath;
    }


}
