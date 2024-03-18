package data;

import enums.ContentType;
import enums.HTTPMethods;

public class HttpRequest {
    private static final String DEFAULT_STRING = "";

    private final HTTPMethods methods;
    private String URL;
    private final ContentType contentType;
    private final boolean isSuccess;

    public HttpRequest(HTTPMethods methods, String url, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.contentType = contentType;
        this.isSuccess = true;

    }

    public HttpRequest() {
        this.methods = HTTPMethods.NONE;
        this.URL = DEFAULT_STRING;
        this.contentType = ContentType.NONE;
        this.isSuccess = false;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public HTTPMethods getMethods() {
        return methods;
    }

    public String getURL() {
        return URL;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "ParsedHttpRequest{" +
                "methods=" + methods +
                ", URL='" + URL + '\'' +
                ", contentType=" + contentType +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
