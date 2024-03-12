package webserver;

import java.io.*;
import java.net.Socket;

import DTO.ParsedHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HTTPParser;
import utils.Paths;


public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final HTTPParser parser = HTTPParser.getInstance();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);

            //파싱된 http request를 DTO로 저장
            ParsedHttpRequest parsedHttpRequest = parser.getParsedHTTP(readHttpRequest(new BufferedReader(new InputStreamReader(in))));

            if (parsedHttpRequest.isSuccess()) {
                ResponseHandler response = new ResponseHandler(this.parser);
                switch (parsedHttpRequest.getMethods()) {
                    case GET -> {
                        logger.info("HTTP METHOD -> GET");
                        logger.debug("Request URL -> {}", parsedHttpRequest.getURL());
                        response.responseGetMethod(Paths.STATIC_RESOURCES + parsedHttpRequest.getURL(), parsedHttpRequest.getContentType(), dos);
                    }
                }
            } else {
                logger.error("CAN NOT Parsing Request!!");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
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
