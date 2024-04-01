package request.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.RequestReader;
import request.data.HttpRequest;
import request.util.constant.RequestKeys;
import utils.ContentType;
import utils.HTTPMethods;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.CRL;
import java.util.*;

import static request.util.constant.RequestKeys.*;
import static utils.StringUtils.CRLF;

public class RequestParser {

    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    private static final RequestParser instance = new RequestParser();
    private static final String FILE_SYMBOL = ".";

    private RequestParser() {

    }

    public static RequestParser getInstance() {
        return instance;
    }

    public HttpRequest getParsedHTTP(Map<String, String> mappingRequest) {
        HttpRequest request;
        try {
            if (mappingRequest.containsKey(CONTENT_LENGTH)) {
                request = parseWithBody(mappingRequest);
            } else request = parseHeaderOnly(mappingRequest);

            //쿠키 있을경우
            if (mappingRequest.containsKey(RequestKeys.COOKIE)) {
                request.addCookie(mappingRequest.get(RequestKeys.COOKIE));
            }

            //url 쿼리문이 있을경우
            if (request.urlHasQuery()) {
                String urlWithQuery = request.getURL();
                StringTokenizer st = new StringTokenizer(urlWithQuery, "?");
                String url = st.nextToken();
                request.setUrl(url);
                while (st.hasMoreTokens()) {
                    String nowQuery = st.nextToken();
                    String[] tmp = nowQuery.split("=");
                    request.addQuery(tmp[0], tmp[1]);
                }
            }

            return request;

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private HttpRequest parseWithBody(Map<String, String> mappingRequest) throws IOException{
        HTTPMethods methods = HTTPMethods.valueOf(mappingRequest.get(METHOD).toUpperCase());
        String url = mappingRequest.get(URL);

        HttpRequest request = new HttpRequest(methods, url);

        //json 인경우
        if (mappingRequest.get(CONTENT_TYPE).contains(ContentType.JSON.getType())) {
            request.setContentType(ContentType.JSON);
            parseJson(request, mappingRequest.get(BODY));
        //url encoded인경우
        }else {
            request.setContentType(ContentType.URL_ENCODED);
            parseUrlEncoded(request, mappingRequest.get(BODY));
        }

        return request;
    }

    private void parseJson(HttpRequest request, String bodyString) {
        StringTokenizer bodyToken = new StringTokenizer(bodyString, ",{}");

        Map<String, String> body = new HashMap<>();
        while (bodyToken.hasMoreTokens()) {
            String nowToken = bodyToken.nextToken().replace("\"", "");
            String[] tmp = nowToken.split(":");
            try {
                body.put(tmp[0].trim(), tmp[1].trim());
            } catch (IndexOutOfBoundsException e) {
                body.put(tmp[0].trim(), "NONE");
            }
        }

        request.setBody(body);
    }


    private void parseUrlEncoded(HttpRequest request, String bodyString) {
        StringTokenizer bodyToken = new StringTokenizer(bodyString, "&");

        Map<String, String> body = new HashMap<>();
        while (bodyToken.hasMoreTokens()) {
            String[] tmp = bodyToken.nextToken().split("=");
            body.put(tmp[0], tmp[1]);
        }

        request.setBody(body);
    }

    private HttpRequest parseHeaderOnly(Map<String, String> mappingRequest) {
        HttpRequest result = new HttpRequest(HTTPMethods.GET, mappingRequest.get(RequestKeys.URL), ContentType.HTML);

        if (mappingRequest.get(RequestKeys.URL).contains(FILE_SYMBOL)) {
            //content-type 파싱 :: 파일을 요청한경우
            ContentType contentType = ContentType.valueOf(mappingRequest.get(RequestKeys.URL).split("\\.")[1].toUpperCase());
            result = new HttpRequest(HTTPMethods.GET, mappingRequest.get(RequestKeys.URL), contentType);
        }
        return result;
    }
}
