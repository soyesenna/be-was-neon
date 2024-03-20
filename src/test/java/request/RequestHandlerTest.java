package request;

import utils.ContentType;
import utils.HTTPMethods;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.data.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;
import static model.UserFiled.*;

public class RequestHandlerTest {


    @Test
    @DisplayName("GET 요청을 잘 읽는지 검사함")
    void readGetRequest() throws IOException {
        String url = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*";
        InputStream in = new ByteArrayInputStream(url.getBytes());
        RequestHandler requestHandler = new RequestHandler(in);
        HttpRequest httpRequest = requestHandler.getRequest();

        assertThat(httpRequest.getMethods()).isEqualTo(HTTPMethods.GET);
        assertThat(httpRequest.getContentType()).isEqualTo(ContentType.HTML);
    }

    @Test
    @DisplayName("POST 요청을 잘 읽는지 검사함. body를 잘 읽어오는지도 검사함")
    void readPostRequest() throws IOException {
        String body = "userId=javajigi&password=password&name=박재성&email=javajigi@slipp.net";
        String url = "POST /user/create HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Accept: */*\r\n" +
                "\r\n" + body;

        InputStream in = new ByteArrayInputStream(url.getBytes());
        RequestHandler requestHandler = new RequestHandler(in);
        HttpRequest httpRequest = requestHandler.getRequest();

        assertThat(httpRequest.getMethods()).isEqualTo(HTTPMethods.POST);
        assertThat(httpRequest.getContentType()).isEqualTo(ContentType.URL_ENCODED);
        assertThat(httpRequest.getBody()).containsKeys(USERID.getFiled(), PASSWORD.getFiled(), NAME.getFiled(), EMAIL.getFiled());
    }

    @Test
    @DisplayName("URL에 파일이 지정되어 있지 않을때 index.html반환해야함")
    void returnIndexIfNoFile() throws IOException {
        String url = "GET /register HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*";
        InputStream in = new ByteArrayInputStream(url.getBytes());
        RequestHandler requestHandler = new RequestHandler(in);
        HttpRequest httpRequest = requestHandler.getRequest();

        assertThat(httpRequest.getURL()).isEqualTo("/register/index.html");
        assertThat(httpRequest.getContentType()).isEqualTo(ContentType.HTML);
    }

    @Test
    @DisplayName("http method 없을때 IOException을 던져야함")
    void throwIOExceptionWhenNoHttpMethod() {
        //http method 없을때
        String noMethod = "/register HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*";
        verifyNoHeader(noMethod);
    }

    @Test
    @DisplayName("요청 url이 없을 때 IOException을 던져야함")
    void throwIOExceptionWhenNoURL() {
        //요청 url이 없을 때
        String noURL = "GET HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*";
        verifyNoHeader(noURL);
    }

    @Test
    @DisplayName("http 버전이 없을 때 IOException을 던져야함")
    void throwIOExceptionNoVersion() {
        //http 버전이 없을 때
        String noVersion = "GET /register\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Accept: */*";
        verifyNoHeader(noVersion);
    }

    void verifyNoHeader(String noHeader) {
        InputStream in = new ByteArrayInputStream(noHeader.getBytes());
        final RequestHandler noMethodRequest = new RequestHandler(in);
        assertThatThrownBy(() -> noMethodRequest.getRequest()).isInstanceOf(IOException.class);
    }

}
