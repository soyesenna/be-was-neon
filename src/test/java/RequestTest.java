import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.util.RequestParser;

public class RequestTest {

    private RequestParser parser;
    @BeforeEach
    void before() {
        parser = RequestParser.getInstance();
    }

    @Test
    @DisplayName("URL에 파일이 지정되어 있지 않을때 index.html반환해야함")
    void returnIndexIfNoFile() {
//        String url = "GET /register HTTP/1.1\n" +
//                "Host: localhost:8080\n" +
//                "Connection: keep-alive\n" +
//                "Accept: */*";
//        try {
//            RequestHandler requestHandler = new RequestHandler()
//            HttpRequest httpRequest = parser.getParsedHTTP(url);
//            assertThat(httpRequest.getURL()).isEqualTo("/register/index.html");
//            assertThat(httpRequest.getContentType()).isEqualTo(ContentType.HTML);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
    }
}
