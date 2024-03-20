package response.util;

import request.data.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.data.HttpResponse;
import utils.Paths;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static utils.StringUtils.*;

public class ResponseMaker {
    private static final Logger logger = LoggerFactory.getLogger(ResponseMaker.class);

    private static final ResponseMaker instance = new ResponseMaker();

    private ResponseMaker() {
    }

    public static ResponseMaker getInstance() {
        return instance;
    }

    public HttpResponse makeResponse(ResponseStatus status, HttpRequest parsingResult){
        //실패했을 시 빈 response 반환
        HttpResponse response = new HttpResponse();

        try {
            switch (status) {
                case OK -> {
                    byte[] body = makeBody(parsingResult);
                    String header = makeHeader(status, parsingResult, body.length);
                    response = new HttpResponse(header, body);
                }
                case REDIRECT -> {
                    String header = makeHeader(status, parsingResult, -1);
                    response = new HttpResponse(header);
                }
            }
        } catch (IOException e) {
            return response;
        }

        return response;
    }

    private byte[] makeBody(HttpRequest parsingResult) throws IOException{
        String urlPath = Paths.STATIC_RESOURCES + parsingResult.getURL();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
            fileReader.lines()
                    .forEach(string -> sb.append(appendHttpEndLine(string)));
        }

        logger.info("Response Body Created");
        return sb.toString().getBytes();
    }

    private String makeHeader(ResponseStatus status, HttpRequest parsingResult, int lengthOfBodyContent) {
        StringBuilder sb = new StringBuilder();

        sb.append(appendHttpEndLine(status.getCode()));
        switch (status) {
            case OK -> {
                sb.append(appendHttpEndLine("Content-Type: " + parsingResult.getContentType().getType()));
                sb.append(appendHttpEndLine("Content-Length: " + lengthOfBodyContent));
            }
            case REDIRECT -> {
                sb.append(appendHttpEndLine("Location: " + DEFAULT_URL));
            }
        }

        logger.info("Response Header Created");
        return sb.toString();
    }
}
