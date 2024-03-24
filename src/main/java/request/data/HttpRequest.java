package request.data;

import utils.ContentType;
import utils.HTTPMethods;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final String DEFAULT_STRING = "";

    private final HTTPMethods methods;
    private String URL;
    private final ContentType contentType;
    private final Map<String, String> body;

    public HttpRequest(HTTPMethods methods, String url, Map<String, String> body, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.body = body;
        this.contentType = contentType;
    }

    public HttpRequest(HTTPMethods methods, String url, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.contentType = contentType;
        this.body = new HashMap<>();
    }

    public HttpRequest() {
        this.methods = HTTPMethods.NONE;
        this.URL = DEFAULT_STRING;
        this.contentType = ContentType.NONE;
        this.body = new HashMap<>();
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

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(this.body);
    }

    @Override
    public String toString() {
        return "ParsedHttpRequest{" +
                "methods=" + methods +
                ", URL='" + URL + '\'' +
                ", contentType=" + contentType +
                '}';
    }
}
