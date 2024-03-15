package Data;

import enums.ContentType;
import enums.HTTPMethods;
import enums.QueryParm;

import java.util.*;

public class ParsedHttpRequest {
    private static final String DEFAULT_STRING = "";

    private final HTTPMethods methods;
    private String URL;
    private final ContentType contentType;
    private final Map<QueryParm, String> query = new EnumMap<>(QueryParm.class);
    private final boolean isSuccess;

    public ParsedHttpRequest(HTTPMethods methods, String url, ContentType contentType) {
        this.methods = methods;
        this.URL = url;
        this.contentType = contentType;
        this.isSuccess = true;

        if (this.contentType.equals(ContentType.QUERY)) parseQuery();
    }

    public ParsedHttpRequest() {
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

    public boolean hasQuery() {
        return !this.query.isEmpty();
    }

    public Map<QueryParm, String> getQuery() {
        return this.query;
    }

    private void parseQuery() {
        StringTokenizer st = new StringTokenizer(this.URL, "?&");
        this.URL = st.nextToken().substring(1);

        while (st.hasMoreTokens()) {
            String[] tmp = st.nextToken().split("=");
            this.query.put(QueryParm.valueOf(tmp[0].toUpperCase()), tmp[1]);
        }
    }

    @Override
    public String toString() {
        return "ParsedHttpRequest{" +
                "methods=" + methods +
                ", URL='" + URL + '\'' +
                ", contentType=" + contentType +
                ", query=" + query +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
