package feed;

import utils.StringUtils;

public class Comment {
    private String writeUserId;
    private String comment;

    public Comment(String writeUserId, String comment) {
        this.writeUserId = writeUserId;
        this.comment = comment;
    }

    public String getWriteUserId() {
        return this.writeUserId;
    }

    public String getComment() {
//        String convertNewLine = this.comment.replace("\\n", StringUtils.CRLF);
        return this.comment;
    }
}
