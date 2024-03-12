import Data.ParsedHttpRequest;
import enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.HTTPParser;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

public class RequestTest {

    @Test
    @DisplayName("URL에 파일이 지정되어 있지 않을때 index.html반환해야함")
    void returnIndexIfNoFile() {
        String url = "GET /register HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*";

        HTTPParser parser = HTTPParser.getInstance();

        try {
            ParsedHttpRequest parsedHttpRequest = parser.getParsedHTTP(url);
            assertThat(parsedHttpRequest.getURL()).isEqualTo("/register/index.html");
            assertThat(parsedHttpRequest.getContentType()).isEqualTo(ContentType.HTML);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
