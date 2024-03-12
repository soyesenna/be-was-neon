package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import DTO.ParsedHttpRequest;
import enums.ContentType;
import enums.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Paths;
import static utils.StringUtils.*;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final int METHOD_INDEX = 0;
    private final int URL_INDEX = 1;


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
            ParsedHttpRequest parsedHttpRequest = getParsedHTTP(readHttpRequest(new BufferedReader(new InputStreamReader(in))));

            if (parsedHttpRequest.isSuccess()) {
                switch (parsedHttpRequest.getMethods()) {
                    case GET -> {
                        logger.info("HTTP METHOD -> GET");
                        logger.debug("Request URL -> {}", parsedHttpRequest.getURL());
                        responseGetMethod(Paths.STATIC_RESOURCES + parsedHttpRequest.getURL(), parsedHttpRequest.getContentType(), dos);
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

    private void responseGetMethod(String urlPath, ContentType contentType, DataOutputStream dos) throws IOException{
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(urlPath)))) {
//            String.valueOf((char) htmlReader.read())
            byte[] body = combineStreamOfByteArrays(
                    fileReader.lines()
                            .map((s) -> appendNewLine(s).getBytes()));

            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
            logger.info("GET Response DONE");
        }
    }

    /**
     * http method와 내용 파싱
     * @param String
     * @return
     * @throws IOException
     */
    private ParsedHttpRequest getParsedHTTP(String request) throws IOException{
        List<String> splitRequest = getTokenizeHTTP(request);

        ParsedHttpRequest result = new ParsedHttpRequest();
        try {
            //유효한 http메서드인지 검사함
            HTTPMethods methods = HTTPMethods.valueOf(splitRequest.get(METHOD_INDEX));
            //유효한 content-type인지 검사함
            try {
                ContentType contentType = ContentType.valueOf(splitRequest.get(URL_INDEX).split("\\.")[1].toUpperCase());
                result = new ParsedHttpRequest(methods, splitRequest.get(URL_INDEX), contentType);
            } catch (IndexOutOfBoundsException notFile) {
                result = new ParsedHttpRequest(methods, splitRequest.get(URL_INDEX), ContentType.NONE);
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

        StringTokenizer st = new StringTokenizer(request, " \r");

        while (st.hasMoreTokens()) tokenizeResult.add(st.nextToken());

        logger.info( "Request Tokenize Done");
        return tokenizeResult;
    }

    private byte[] combineStreamOfByteArrays(Stream<byte[]> stream) {
        return stream.reduce(new byte[0], (a, b) -> {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        });
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
