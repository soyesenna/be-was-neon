package feed;

import model.User;

public class Comment {
    private User writeUser;
    private String comment;

    public Comment(User writeUser, String comment) {
        this.writeUser = writeUser;
        this.comment = comment;
    }

    public String getWriteUserName() {
        return this.writeUser.getName();
    }

    public String getComment() {
        return this.comment;
    }

    public boolean writeUserHasProfile() {
        return this.writeUser.hasProfileImage();
    }

    public String writeUserProfileImg() {
        return writeUser.getProfileImgPath();
    }
}
