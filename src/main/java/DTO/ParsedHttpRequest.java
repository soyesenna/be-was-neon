package DTO;

import enums.ContentType;
import enums.HTTPMethods;

public class ParsedHttpRequest {

    private final HTTPMethods methods;
    private final String URL;
    private final ContentType contentType;
    private final boolean isSuccess;

    public ParsedHttpRequest(HTTPMethods methods, String url, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.contentType = contentType;
        this.isSuccess = true;
    }

    public ParsedHttpRequest() {
        this.methods = HTTPMethods.NONE;
        this.URL = "";
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
}
