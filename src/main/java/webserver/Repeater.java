package webserver;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Map;

import property.Properties;
import property.Property;
import property.RunnableMethod;
import request.data.HttpRequest;
import request.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import response.ResponseSender;
import response.data.HttpResponse;

import javax.xml.crypto.Data;


public class Repeater implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Repeater.class);
    private static final Properties properties = Properties.getInstance();
    private final Socket connection;

    public Repeater(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request;
            //요청을 읽고 파싱
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            RequestReader reader = new RequestReader(bufferedReader);
            request = reader.readHttpRequest();

            //응답 객체 생성
            HttpResponse response = new HttpResponse();

            //현재 요청에 대한 processor 가져옴
            RunnableMethod nowProcessor = properties.getProcessingMethodByProperty(Property.of(request.getMethods(), request.getURL()));
            //url에 따른 요청 실행
            methodInvoke(nowProcessor, request, response);
            logger.debug("Processing Done");

            //요청 실행 후 만들어진 응답 send
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            ResponseSender sender = new ResponseSender(response, dataOutputStream);
            sender.doResponse();

            //응답 보낸 후 연결 종료
            bufferedReader.close();
            dataOutputStream.close();
        }
        catch (IOException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    private void methodInvoke(RunnableMethod nowProcessor, HttpRequest request, HttpResponse response) throws InvocationTargetException, IllegalAccessException {
        nowProcessor.method().invoke(nowProcessor.instance().invoke(null), request, response);
    }

}

