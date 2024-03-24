package request.util;

import utils.ContentType;
import utils.HTTPMethods;
import model.UserFiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.data.HttpRequest;
import request.util.constant.RequestKeys;

import java.io.IOException;
import java.util.*;

public class RequestParser {
    private static final String DEFAULT_URL = "/index.html";
    private static final String FILE_SYMBOL = ".";
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    private static final RequestParser instance = new RequestParser();
    private static final String ILLEGAL_BODY_MESSAGE = "잘못된 body입니다";

    private RequestParser() {

    }

    public static RequestParser getInstance() {
        return instance;
    }

    /**
     * http method와 내용 파싱
     * @param request
     * @return
     * @throws IOException
     */
    public HttpRequest getParsedHTTP(Map<String, String> request) throws IOException{
        //유효하지 않은 http request일 경우 리턴될 객체
        HttpRequest result = new HttpRequest();
        try {
            //유효한 http메서드인지 검사함
            HTTPMethods methods = HTTPMethods.valueOf(request.get(RequestKeys.METHOD));

            switch (methods) {
                case GET -> result = parseGet(request);
                case POST -> result = parsePost(request);
            }

            //쿠키 있을경우
            if (request.containsKey(RequestKeys.COOKIE)) {
                result.addCookie(request.get(RequestKeys.COOKIE));
            }

        } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage());
        }

        logger.debug(result.getURL());

        return result;
    }

    private HttpRequest parsePost(Map<String, String> request) throws IllegalArgumentException{
        StringTokenizer bodyToken = new StringTokenizer(request.get(RequestKeys.BODY), "&");

        Map<String, String> body = new HashMap<>();
        try {
            //body에 User필드에 없는 값이 들어오는지 검사하기위해
            List<String> requireFields = UserFiled.getFiledNames();
            while (bodyToken.hasMoreTokens()) {
                String[] tmp = bodyToken.nextToken().split("=");
                if (!requireFields.contains(tmp[0])) throw new IllegalArgumentException(ILLEGAL_BODY_MESSAGE);
                body.put(tmp[0], tmp[1]);
            }
        }catch (IndexOutOfBoundsException e){
            throw new IllegalArgumentException(ILLEGAL_BODY_MESSAGE);
        }

        return new HttpRequest(HTTPMethods.POST, request.get(RequestKeys.URL), body, ContentType.URL_ENCODED);
    }

    private HttpRequest parseGet(Map<String, String> request) throws IllegalArgumentException{
        HttpRequest result = new HttpRequest(HTTPMethods.GET, request.get(RequestKeys.URL), ContentType.HTML);

        if (request.get(RequestKeys.URL).contains(FILE_SYMBOL)) {
            //content-type 파싱 :: 파일을 요청한경우
            ContentType contentType = ContentType.valueOf(request.get(RequestKeys.URL).split("\\.")[1].toUpperCase());
            result = new HttpRequest(HTTPMethods.GET, request.get(RequestKeys.URL), contentType);
        }
        return result;
    }
}
