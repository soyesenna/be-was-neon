package request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.data.HttpRequest;
import request.util.RequestParser;
import request.util.RequestReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final RequestParser parser = RequestParser.getInstance();
    private final RequestReader reader = RequestReader.getInstance();
    private final InputStream in;

    public RequestHandler(InputStream in) {
        this.in = in;
    }

    public HttpRequest getRequest() throws IOException{
        BufferedReader requestStream = new BufferedReader(new InputStreamReader(in));

        Map<String, String> readResult = reader.readHttpRequest(requestStream);
        logger.debug("Request Read Done");

        HttpRequest parseResult = parser.getParsedHTTP(readResult);
        logger.debug("Request Parsing Done");

        return parseResult;
    }


}
