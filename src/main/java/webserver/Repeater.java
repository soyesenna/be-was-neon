package webserver;

import java.io.*;
import java.net.Socket;

import data.HttpRequest;
import handler.RequestHandler;
import handler.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Repeater implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Repeater.class);
    private Socket connection;

    public Repeater(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            RequestHandler requestHandler = new RequestHandler(in);
            //파싱된 http request를 저장
            HttpRequest httpRequest = requestHandler.getRequest();

            if (httpRequest.isSuccess()) {
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

