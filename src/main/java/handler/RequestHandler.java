package handler;

import data.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RequestParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final RequestParser parser = RequestParser.getInstance();
    private final InputStream in;

    public RequestHandler(InputStream in) {
        this.in = in;
    }

    public HttpRequest getRequest() throws IOException{
        return  parser.getParsedHTTP(readHttpRequest(new BufferedReader(new InputStreamReader(in))));
    }

    private String readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        StringBuilder httpRequest = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            httpRequest.append(line);
            if (line.isEmpty()) {
                break;
            }
        }
        logger.info("Request Read Done");
        return httpRequest.toString();
    }
}
