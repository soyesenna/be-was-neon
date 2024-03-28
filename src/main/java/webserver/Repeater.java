package webserver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import processors.ProcessorHandler;
import request.data.HttpRequest;
import request.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.ResponseSender;
import response.data.HttpResponse;


public class Repeater implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Repeater.class);

    private final Socket connection;

    public Repeater(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest();
            //요청을 읽고 파싱
            RequestReader reader = new RequestReader(new BufferedReader(new InputStreamReader(in)));
            request = reader.readHttpRequest();
            logger.info("Request Read And Parsing Done");

            //응답 객체 생성
            HttpResponse response = new HttpResponse();

            //processor hanlder에게 request처리 후 response에 결과 주입 요청
            ProcessorHandler processorHandler = new ProcessorHandler(request, response);
            processorHandler.doProcess();

            //요청 실행 후 만들어진 응답 send
            ResponseSender sender = new ResponseSender(response, new DataOutputStream(out));
            sender.doResponse();
            logger.info("Response Send Done");
        }
        catch (IOException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

}

