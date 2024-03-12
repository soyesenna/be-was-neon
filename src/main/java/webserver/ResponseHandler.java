package webserver;

import enums.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HTTPParser;

import java.io.*;

import static utils.StringUtils.appendNewLine;

public class ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private final HTTPParser parser;

    public ResponseHandler(HTTPParser parser) {
        this.parser = parser;
    }

    public void responseGetMethod(String urlPath, ContentType contentType, DataOutputStream dos) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
//            String.valueOf((char) htmlReader.read())
            byte[] body = parser.combineStreamOfByteArrays(
                    fileReader.lines()
                            .map((s) -> appendNewLine(s).getBytes()));

            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
            logger.info("GET Response DONE");
        }
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, ContentType contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType.getType() + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
