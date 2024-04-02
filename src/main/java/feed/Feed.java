package feed;

import model.User;
import utils.ContentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Feed {

    private User uploader;
    private String contents;
    private String imagePath;
    private ContentType imageType;
    private List<Comment> comments = new ArrayList<>();

    public Feed(User uploader, String imagePath, ContentType imageType) {
        this.uploader = uploader;
        this.imagePath = imagePath;
        this.contents = "";
        this.imageType = imageType;
    }
    public Feed(User uploader, String imagePath, ContentType imageType,  String contents) {
        this.uploader = uploader;
        this.contents = contents;
        this.imagePath = imagePath;
        this.imageType = imageType;
    }

    public String getUploaderName() {
        return this.uploader.getName();
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public String getContents() {
        return this.contents;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(this.comments);
    }


}
