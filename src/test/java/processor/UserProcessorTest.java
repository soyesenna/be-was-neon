package processor;

import static model.UserFiled.*;
import static org.assertj.core.api.Assertions.*;

import db.Database;
import db.Session;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import processors.UserProcessor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.HttpStatus;
import utils.ContentType;
import utils.HTTPMethods;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class UserProcessorTest {

    private UserProcessor userProcessor;

    @BeforeEach
    void setUp() {
        userProcessor = UserProcessor.getInstance();
        Session.clear();
    }

    @Test
    @DisplayName("회원가입 페이지 요청 처리")
    void testRegisterPage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/user/registration", ContentType.HTML);
        HttpResponse response = new HttpResponse();

        userProcessor.registerPage(request, response);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(response.getBody(), 0, response.getBody().length);

        assertThat(baos.toString().contains("/user/registration")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(request.getMethods()).isEqualTo(HTTPMethods.GET);
    }

    @Test
    @DisplayName("로그인 페이지 요청 처리")
    void testLoginPage() {
        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/user/login", ContentType.HTML);
        HttpResponse response = new HttpResponse();

        userProcessor.loginPage(request, response);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(response.getBody(), 0, response.getBody().length);

        assertThat(baos.toString().contains("회원가입")).isTrue();
    }

    @Test
    @DisplayName("회원가입 요청 처리")
    void testRegister() {
        Map<String, String> body = Map.of(
                USERID.getFiled(), "lee",
                PASSWORD.getFiled(), "pass",
                EMAIL.getFiled(), "lee@naver.com",
                NAME.getFiled(), "이주영"
        );

        HttpRequest request = new HttpRequest(HTTPMethods.POST, "/user/create", ContentType.URL_ENCODED);
        request.setBody(body);
        HttpResponse response = new HttpResponse();

        userProcessor.register(request, response);

        assertThat(response.getHeader()).contains(HttpStatus.REDIRECT.getCode());
        assertThat(Database.findUserById("lee")).isEqualTo(new User("lee", "pass", "이주영", "lee@naver.com"));
    }

    @Test
    @DisplayName("로그인 요청 처리")
    void testLogin() {
        // 로그인 테스트를 위해 유저 추가
        Database.addUser(new User("lee", "pass", "이주영", "lee@naver.com"));

        Map<String, String> body = Map.of(
                USERID.getFiled(), "lee",
                PASSWORD.getFiled(), "pass"
        );

        HttpRequest request = new HttpRequest(HTTPMethods.POST, "/user/login", ContentType.URL_ENCODED);
        request.setBody(body);
        HttpResponse response = new HttpResponse();

        userProcessor.login(request, response);

        assertThat(response.getHeader()).contains("Set-Cookie");
        assertThat(response.getHeader()).contains(HttpStatus.REDIRECT.getCode());
        assertThat(Session.getSessionSize()).isEqualTo(1);
    }
}
