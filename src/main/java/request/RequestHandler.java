package request;

import enums.global.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.data.HttpRequest;
import request.util.RequestParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import static request.util.constant.RequestKeys.*;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final RequestParser parser = RequestParser.getInstance();
    private final InputStream in;

    public RequestHandler(InputStream in) {
        this.in = in;
    }

    public HttpRequest getRequest() throws IOException{
        return parser.getParsedHTTP(readHttpRequest(new BufferedReader(new InputStreamReader(in))));
    }

    private Map<String, String> readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequest = new HashMap<>();

        //header start line
        String[] startLine = bufferedReader.readLine().split(" ");
        try {
            httpRequest.put(METHOD, startLine[0]);
            httpRequest.put(URL, startLine[1]);
            httpRequest.put(HTTP_VERSION, startLine[2]);
        }catch (IndexOutOfBoundsException e){
            throw new IOException("잘못된 header입니다");
        }

        //header
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) break;
            String[] tmp = line.split(" ");
            httpRequest.put(tmp[0].replace(":", ""), tmp[1]);
        }

        logger.debug("Request Header Read");
        //body
        if (httpRequest.get(METHOD).equals(HTTPMethods.POST.name())) {
            int bodyLength = Integer.parseInt(httpRequest.get(CONTENT_LENGTH));

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bodyLength; i++) sb.append((char) bufferedReader.read());

            httpRequest.put(BODY, sb.toString());
        }
        logger.info("Request Read Done");
        return httpRequest;
    }
}
