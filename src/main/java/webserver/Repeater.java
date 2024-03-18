package webserver;

import java.io.*;
import java.net.Socket;

import enums.global.HTTPMethods;
import request.data.HttpRequest;
import request.RequestHandler;
import response.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Repeater implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Repeater.class);
    private Socket connection;
    private final PostProcessor postProcessor = PostProcessor.getInstance();

    public Repeater(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            RequestHandler requestHandler = new RequestHandler(in);
            //파싱된 http request를 저장
            HttpRequest httpRequest = requestHandler.getRequest();

            if (httpRequest.isSuccess()) {
                //post인경우 요청사항 처리
                if (httpRequest.getMethods().equals(HTTPMethods.POST))
                    postProcessor.process(httpRequest.getURL(), httpRequest.getBody());

                //요청에 따라 응답
                ResponseHandler responseHandler = new ResponseHandler(httpRequest, out);
                responseHandler.doResponse();
            } else {
                logger.error("CAN NOT Parsing Request!!");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


}

