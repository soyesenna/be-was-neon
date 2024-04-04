package processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import processors.FileProcessor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.HttpStatus;
import utils.ContentType;
import utils.HTTPMethods;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.assertj.core.api.ByteArrayAssert;

public class FileProcessorTest {

    private FileProcessor fileProcessor;

    @BeforeEach
    void setUp() {
        fileProcessor = FileProcessor.getInstance();
    }

    @Test
    @DisplayName("정적 파일 요청 처리")
    void testResponseFile() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/main.css", ContentType.CSS);
        HttpResponse response = new HttpResponse();

        fileProcessor.responseFile(request, response);


        ByteArrayAssert byteArrayAssert = new ByteArrayAssert(response.getBody());
        byteArrayAssert.asString().contains("wrapper");

        assertThat(response.getHeader()).contains(ContentType.CSS.getType());
    }

    @Test
    @DisplayName("세션에 sid 정보가 없을 때 메인 페이지 요청 시 로그인 페이지로 리다이렉트")
    void testRedirectToLoginPageWhenNoSessionId() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/", ContentType.HTML);
        HttpResponse response = new HttpResponse();

        fileProcessor.mainPage(request, response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REDIRECT.getCode());
        assertThat(response.getHeader()).contains("/user/login");
    }

}
