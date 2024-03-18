package request;

import enums.ContentType;
import enums.HTTPMethods;
import model.UserFiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        //isSuccess == false임
        HttpRequest result = new HttpRequest();
        try {
            //유효한 http메서드인지 검사함
            HTTPMethods methods = HTTPMethods.valueOf(request.get(RequestKeys.METHOD));

            switch (methods) {
                case GET -> result = parseGet(request);
                case POST -> result = parsePost(request);
            }

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }

        logger.debug(result.getURL());

        //파싱이 잘못되면 result.isSuccess == false
        logger.info("Request Parsing DONE");
        logger.debug("Parsing Success ? {}",  result.isSuccess());
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

        return new HttpRequest(HTTPMethods.POST, request.get(RequestKeys.URL), body);
    }

    private HttpRequest parseGet(Map<String, String> request) {
        HttpRequest result;
        if (request.get(RequestKeys.URL).contains(FILE_SYMBOL)) {
            //content-type 파싱 :: 파일을 요청한경우
            ContentType contentType = ContentType.valueOf(request.get(RequestKeys.URL).split("\\.")[1].toUpperCase());
            result = new HttpRequest(HTTPMethods.GET, request.get(RequestKeys.URL), contentType);
        }else {
            //file이 아닌 기본 url일 경우 index.html 매핑
            String url = request.get(RequestKeys.URL) + DEFAULT_URL;
            result = new HttpRequest(HTTPMethods.GET, url, ContentType.HTML);
        }
        return result;
    }
}
