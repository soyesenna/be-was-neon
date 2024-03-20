package response;

import request.data.HttpRequest;
import response.util.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.data.HttpResponse;
import response.util.ResponseMaker;
import utils.StringUtils;

import java.io.*;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private final HttpRequest parsingResult;
    private final DataOutputStream dos;
    private final ResponseMaker responseMaker = ResponseMaker.getInstance();

    public ResponseHandler(HttpRequest parsingResult, OutputStream out) {
        this.parsingResult = parsingResult;
        this.dos = new DataOutputStream(out);
    }

    public void doResponse() throws IOException{
        logger.info("HTTP METHOD -> {}", parsingResult.getMethods());
        logger.debug("Request URL -> {}", parsingResult.getURL());
        switch (parsingResult.getMethods()) {
            case GET -> responseGetMethod();
            case POST -> responsePostMethod();
        }
    }

    private void responsePostMethod() {
        HttpResponse httpResponse = responseMaker.makeResponse(ResponseStatus.REDIRECT, parsingResult);
        try {
            if (!httpResponse.isSuccess()) throw new IOException("Response를 만드는데 실패했습니다");
            writeResponse(httpResponse);
        } catch (IOException e){
            logger.error(e.getMessage());
        }

        logger.debug("{}로 Redirect됨", StringUtils.DEFAULT_URL);
        logger.info("POST Response Done");
    }

    private void responseGetMethod() {
        HttpResponse httpResponse = responseMaker.makeResponse(ResponseStatus.OK, parsingResult);
        try {
            if (!httpResponse.isSuccess()) throw new IOException("Response를 만드는데 실패했습니다");
            writeResponse(httpResponse);
        } catch (IOException e){
            logger.error(e.getMessage());
        }

        logger.debug("Response 된 파일 = {}", parsingResult.getURL());
        logger.info("GET Response DONE");
    }

    private void writeResponse(HttpResponse response) throws IOException{
        dos.writeBytes(response.getHeader());
        dos.writeBytes(StringUtils.END_OF_HTTP_LINE);
        if (response.hasBody()) dos.write(response.getBody());
        dos.flush();
    }

}

