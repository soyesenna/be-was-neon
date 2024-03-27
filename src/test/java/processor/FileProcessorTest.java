package processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import processors.FileProcessor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.ResponseStatus;
import utils.ContentType;
import utils.HTTPMethods;
import utils.Paths;
import utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileProcessorTest {

    private FileProcessor fileProcessor;
    private byte[] registerPage;
    private byte[] welcomePage;

    @BeforeEach
    void before() throws Exception {
        fileProcessor = FileProcessor.getInstance();

        //register page init
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(Paths.STATIC_RESOURCES + "/registration/index.html"));
        reader.lines()
                .forEach(string -> sb.append(StringUtils.appendCRLF(string)));
        registerPage = sb.toString().getBytes();

        //welcome page init
        StringBuilder sb2 = new StringBuilder();
        reader = new BufferedReader(new FileReader(Paths.STATIC_RESOURCES + "/index.html"));
        reader.lines()
                .forEach(string -> sb2.append(StringUtils.appendCRLF(string)));
        welcomePage = sb2.toString().getBytes();
    }

    @Test
    @DisplayName("url -> '/' 일 경우 static/index.html 반환")
    void welcomePage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/", ContentType.HTML);

        HttpResponse response = new HttpResponse();
        fileProcessor.welcomePage(request, response);

        assertThat(response.getHeader()).contains(ResponseStatus.OK.getCode());
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getBody()).isEqualTo(welcomePage);
    }

    @Test
    @DisplayName("파일을 직접 요청한 경우 경로 그대로 파일 읽어서 반환")
    void fileResponse() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/reset.css", ContentType.CSS);

        HttpResponse response = new HttpResponse();
        fileProcessor.responseFile(request, response);

        assertThat(response.hasBody()).isTrue();
        assertThat(response.getHeader()).contains(ContentType.CSS.getType());
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("/registration 요청 시 static/registration/index.html 반환")
    void registerPage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/registration", ContentType.HTML);

        HttpResponse response = new HttpResponse();
        fileProcessor.registerPage(request, response);

        assertThat(response.hasBody()).isTrue();
        assertThat(response.getHeader()).contains(ContentType.HTML.getType());
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).isEqualTo(registerPage);
    }
}
