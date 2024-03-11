package utils;

import Data.ParsedHttpRequest;
import enums.ContentType;
import enums.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class HTTPParser {
    private static final String DEFAULT_URL = "/index.html";
    private static final String FILE_SYMBOL = ".";
    private static final String QUERY_SYMBOL = "?";
    private static final Logger logger = LoggerFactory.getLogger(HTTPParser.class);
    private static final HTTPParser instance = new HTTPParser();

    private final int METHOD_INDEX = 0;
    private final int URL_INDEX = 1;

    private HTTPParser() {

    }

    public static HTTPParser getInstance() {
        return instance;
    }

    /**
     * http method와 내용 파싱
     * @param request
     * @return
     * @throws IOException
     */
    public ParsedHttpRequest getParsedHTTP(String request) throws IOException{
        List<String> splitRequest = getTokenizeHTTP(request);

        //유효하지 않은 http request일 경우 리턴될 객체
        //isSuccess == false임
        ParsedHttpRequest result = new ParsedHttpRequest();
        try {
            //유효한 http메서드인지 검사함
            HTTPMethods methods = HTTPMethods.valueOf(splitRequest.get(METHOD_INDEX));

            if (splitRequest.get(URL_INDEX).contains(QUERY_SYMBOL)) {
                //쿼리 파싱
                result = new ParsedHttpRequest(methods, splitRequest.get(URL_INDEX), ContentType.QUERY);
            } else if (splitRequest.get(URL_INDEX).contains(FILE_SYMBOL)) {
                //content-type 파싱 :: 파일을 요청한경우
                ContentType contentType = ContentType.valueOf(splitRequest.get(URL_INDEX).split("\\.")[1].toUpperCase());
                result = new ParsedHttpRequest(methods, splitRequest.get(URL_INDEX), contentType);

            }else {
                //file, query가 아닌 기본 url일 경우 index.html 매핑
                String url = splitRequest.get(URL_INDEX) + DEFAULT_URL;
                result = new ParsedHttpRequest(methods, url, ContentType.HTML);
            }

        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }

        //파싱이 잘못되면 result.isSuccess == false
        logger.info("Request Parsing DONE");
        logger.debug("Parsing Success ? {}",  result.isSuccess());
        return result;
    }

    private List<String> getTokenizeHTTP(String request)throws IOException{
        List<String> tokenizeResult = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(request, " ");

        while (st.hasMoreTokens()) tokenizeResult.add(st.nextToken());

        logger.info("Request Tokenize Done");
        return tokenizeResult;
    }

    public byte[] combineStreamOfByteArrays(Stream<byte[]> stream) {
        return stream.reduce(new byte[0], (a, b) -> {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        });
    }
}