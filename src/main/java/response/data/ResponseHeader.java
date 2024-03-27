package response.data;

import utils.ContentType;

import static utils.StringUtils.*;

public class ResponseHeader {

    private static final String DELETE_COOKIE_FORMAT = "sid=%s;max-age=0";
    private static final String SET_COOKIE_FORMAT = "sid=%s;path=/";

    private int contentLength;
    private ContentType contentType;
    private String cookie;
    private String redirectPath;

    public ResponseHeader() {
        this.contentLength = 0;
        this.contentType = ContentType.NONE;
        this.cookie = "";
        this.redirectPath = "";
    }

    public void setRedirectPath(String path) {
        this.redirectPath = path;
    }

    public void setContentType(ContentType type) {
        this.contentType = type;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setCookie(String id) {
        this.cookie = String.format(SET_COOKIE_FORMAT, id);
    }

    public void deleteCookie(String id) {
        this.cookie = String.format(DELETE_COOKIE_FORMAT, id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.cookie.isEmpty()) sb.append("Set-Cookie: ").append(this.cookie).append(CRLF);

        if (!redirectPath.isEmpty()) {
            sb.append("Location: ").append(redirectPath).append(CRLF);
            return sb.toString();
        }

        if (!contentType.equals(ContentType.NONE)) {
            sb.append("Content-Length: ").append(contentLength).append(CRLF);
            sb.append("Content-Type: ").append(contentType.getType()).append(CRLF);
        }

        return sb.toString();
    }
}
