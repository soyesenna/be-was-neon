package request.data;

import utils.ContentType;
import utils.HTTPMethods;

import java.util.*;

public class HttpRequest {
    private static final String DEFAULT_STRING = "";

    private final HTTPMethods methods;
    private String URL;
    private ContentType contentType;
    private Map<String, String> body; //name=value
    private Map<String, String> cookie;
    private Map<String, String> query;

    public HttpRequest(HTTPMethods methods, String url) {
        this.methods = methods;
        this.URL = url;
        this.cookie = new HashMap<>();
        this.query = new HashMap<>();
    }

    public HttpRequest(HTTPMethods methods, String url, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.contentType = contentType;
        this.body = new HashMap<>();
        this.cookie = new HashMap<>();
        this.query = new HashMap<>();
    }

    public HttpRequest() {
        this.methods = HTTPMethods.NONE;
        this.URL = DEFAULT_STRING;
        this.contentType = ContentType.NONE;
        this.body = new HashMap<>();
        this.cookie = new HashMap<>();
        this.query = new HashMap<>();
    }

    public HTTPMethods getMethods() {
        return methods;
    }

    public String getURL() {
        return URL;
    }

    public void setUrl(String url) {
        this.URL = url;
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(this.body);
    }

    public void addCookie(String cookies) {
        StringTokenizer st = new StringTokenizer(cookies, "; ");
        while (st.hasMoreTokens()) {
            String nowCookie = st.nextToken();
            String[] tmp = nowCookie.split("=");
            this.cookie.put(tmp[0], tmp[1]);
        }
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public void setContentType(ContentType type) {
        this.contentType = type;
    }

    public Map<String, String> getCookie() {
        return this.cookie;
    }

    public void addQuery(String key, String value) {
        this.query.put(key, value);
    }

    public boolean urlHasQuery() {
        return this.URL.contains("?");
    }

    public String getQueryValue(String key) {
        return this.query.get(key);
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
