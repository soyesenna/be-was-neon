package request;

import request.data.HttpRequest;
import request.util.RequestParser;
import utils.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static request.util.constant.RequestKeys.*;
import static request.util.constant.RequestKeys.BODY;

public class RequestReader {
    private static final Logger logger = LoggerFactory.getLogger(RequestReader.class);

    private final RequestParser parser = RequestParser.getInstance();

    private final BufferedReader inputStream;


    public RequestReader(BufferedReader inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest readHttpRequest() throws IOException {
        Map<String, String> readResult = readInputStream();

        HttpRequest parsedHTTP = parser.getParsedHTTP(readResult);

        return parsedHTTP;
    }

    private Map<String, String> readInputStream() throws IOException{
        Map<String, String> httpRequest = new HashMap<>();

        //header start line
        String[] startLine = inputStream.readLine().split(" ");
        try {
            httpRequest.put(METHOD, startLine[0]);
            httpRequest.put(URL, startLine[1]);
            httpRequest.put(HTTP_VERSION, startLine[2]);
        }catch (IndexOutOfBoundsException e){
            throw new IOException("잘못된 header입니다");
        }

        //header
        String line;
        while ((line = inputStream.readLine()) != null) {
            if (line.isEmpty()) break;
            String[] tmp = line.split(":");

            String value = "";
            for (int i = 1; i < tmp.length; i++) value += tmp[i];
            value = value.trim();

            httpRequest.put(tmp[0], value);
        }

        logger.debug("Request Header Read");
        //body
        if (httpRequest.get(METHOD).equals(HTTPMethods.POST.name())) {
            int bodyLength = Integer.parseInt(httpRequest.get(CONTENT_LENGTH));

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bodyLength; i++) sb.append((char) inputStream.read());

            httpRequest.put(BODY, sb.toString());

            logger.debug("Request Body Read");
        }

        return httpRequest;
    }
}
