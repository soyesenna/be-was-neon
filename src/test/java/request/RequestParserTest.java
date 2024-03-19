package request;

import enums.global.ContentType;
import enums.global.HTTPMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.data.HttpRequest;
import request.util.RequestParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static model.UserFiled.*;
import static model.UserFiled.EMAIL;
import static request.util.constant.RequestKeys.*;
import static org.assertj.core.api.Assertions.*;

public class RequestParserTest {

    private RequestParser parser = RequestParser.getInstance();
    private Map<String, String> httpRequest;

    @BeforeEach
    void initRequest() {
        //기본은 post로 진행
        //테스트할때 replace로 바꿔서 진행할 예정
        String body = "userId=javajigi&password=password&name=박재성&email=javajigi@slipp.net";

        httpRequest = new HashMap<>();
        httpRequest.put(METHOD, "POST");
        httpRequest.put(URL, "/user/create");
        httpRequest.put(HTTP_VERSION, "HTTP/1.1");

        httpRequest.put(HOST, "localhost:8080");
        httpRequest.put(CONNECTION, "keep-alive");
        httpRequest.put(CONTENT_LENGTH, String.valueOf(body.length()));
        httpRequest.put(CONTENT_TYPE, ContentType.URL_ENCODED.getType());
        httpRequest.put(ACCEPT, "*/*");

        httpRequest.put(BODY, body);
    }

    @Test
    @DisplayName("POST파싱이 잘되는지 검증")
    void parsePost() throws IOException {
        HttpRequest request = parser.getParsedHTTP(httpRequest);

        verifyRequest(request, HTTPMethods.POST, "/user/create", ContentType.URL_ENCODED);
        assertThat(request.getBody()).containsKeys(USERID.getFiled(), PASSWORD.getFiled(), NAME.getFiled(), EMAIL.getFiled());
    }

    @Test
    @DisplayName("GET파싱이 잘되는지 검증")
    void postGet() throws IOException {
        //GET 요청으로 변환
        convertToGet();
        httpRequest.replace(URL, "/register/index.html");

        HttpRequest request = parser.getParsedHTTP(httpRequest);
        verifyRequest(request, HTTPMethods.GET, "/register/index.html", ContentType.HTML);
    }

    @Test
    @DisplayName("GET요청할때 요청 파일을 지정하지 않으면 index.html로 매핑되는지 검증")
    void mappingIndexIfNoFIle() throws IOException{
        //GET 요청으로 변환
        convertToGet();
        //파일 요청 안하도록
        httpRequest.replace(URL, "/register");

        HttpRequest request = parser.getParsedHTTP(httpRequest);
        verifyRequest(request, HTTPMethods.GET, "/register/index.html", ContentType.HTML);
    }

    void convertToGet() {
        httpRequest.replace(METHOD, "GET");
        httpRequest.replace(CONTENT_TYPE, ContentType.HTML.getType());

        httpRequest.remove(CONTENT_LENGTH);
        httpRequest.remove(BODY);
    }

    void verifyRequest(HttpRequest request, HTTPMethods methods, String url, ContentType contentType) {
        assertThat(request.getMethods()).isEqualTo(methods);
        assertThat(request.getURL()).isEqualTo(url);
        assertThat(request.getContentType()).isEqualTo(contentType);
    }

    @Test
    @DisplayName("http메서드가 잘못 들어왔을 때 예외 던지는지 검증")
    void noneHttpMethod() {
        //잘못된 http메서드
        httpRequest.replace(METHOD, "POS");
        assertThatThrownBy(() -> parser.getParsedHTTP(httpRequest)).isInstanceOf(IOException.class);
    }
}
