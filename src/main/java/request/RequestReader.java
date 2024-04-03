package request;

import request.data.HttpRequest;
import request.util.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static request.util.constant.RequestKeys.*;
import static request.util.constant.RequestKeys.BODY;

public class RequestReader {
    private static final Logger logger = LoggerFactory.getLogger(RequestReader.class);

    private final RequestParser parser = RequestParser.getInstance();

    private final BufferedInputStream inputStream;


    public RequestReader(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest readHttpRequest() throws IOException {
        Map<String, String> readResult = readInputStream();

        HttpRequest parsedHTTP = parser.getParsedHTTP(readResult);

        return parsedHTTP;
    }

    private Map<String, String> readInputStream() throws IOException{
        Map<String, String> httpRequest = new HashMap<>();

        //request read
        int bufSize = 1024;
        byte[] buf;
        int readSize = bufSize + 1;
        ByteArrayOutputStream toHeader = new ByteArrayOutputStream();
        ByteArrayOutputStream toBody = new ByteArrayOutputStream();
        while (readSize >= bufSize) {
            buf = new byte[bufSize];
            readSize = inputStream.read(buf, 0, bufSize);
            toHeader.write(buf);
            toBody.write(buf);
        }
        String toHeaderString = toHeader.toString(StandardCharsets.UTF_8).trim();
        toHeader.close();
        if (toHeaderString.isEmpty()) throw new IOException();

        logger.debug(toHeaderString);
        //header와 body 분리
        String[] tmp = toHeaderString.split(StringUtils.CRLF + StringUtils.CRLF);
        //헤더 매핑
        mappingHeader(httpRequest, tmp[0]);

        logger.debug(httpRequest.get(URL));

        //make body
        if (httpRequest.containsKey(CONTENT_LENGTH)) {
            httpRequest.put(BODY, tmp[1]);
        }

        toBody.close();

        return httpRequest;
    }

    private void mappingHeader(Map<String, String> result, String header) {
        StringTokenizer headerToken = new StringTokenizer(header, StringUtils.CRLF);

        //start line 매핑
        StringTokenizer startLine = new StringTokenizer(headerToken.nextToken());
        result.put(METHOD, startLine.nextToken());
        result.put(URL, startLine.nextToken());
        result.put(HTTP_VERSION, startLine.nextToken());

        //나머지 header 매핑
        StringTokenizer headerDetail;
        while (headerToken.hasMoreTokens()) {
            headerDetail = new StringTokenizer(headerToken.nextToken(), ":");
            String key = headerDetail.nextToken().trim();
            String value = headerDetail.nextToken().trim();
            if (headerDetail.hasMoreTokens()) value += ":" + headerDetail.nextToken();
            result.put(key, value);
        }
    }
}
