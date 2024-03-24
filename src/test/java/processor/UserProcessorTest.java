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
import response.util.ResponseStatus;
import utils.ContentType;
import utils.HTTPMethods;
import java.util.Map;

public class UserProcessorTest {

    private UserProcessor userProcessor;
    private Session session;

    @BeforeEach
    void before() throws Exception{
        userProcessor = UserProcessor.getInstance();
        session = Session.getInstance();
    }

    @Test
    @DisplayName("POST /user/create 요청 시 body로 보낸 데이터로 회원가입 되어야함")
    void userCreate() {
        Map<String, String> body = Map.of(
                USERID.getFiled(), "kim",
                PASSWORD.getFiled(), "1234",
                EMAIL.getFiled(), "a@naver.com",
                NAME.getFiled(), "김주영"
        );

        HttpRequest request = new HttpRequest(HTTPMethods.POST, "/user/crate", body, ContentType.URL_ENCODED);

        HttpResponse response = new HttpResponse();
        userProcessor.register(request, response);

        assertThat(response.getHeader()).contains(ResponseStatus.REDIRECT.getCode());
        assertThat(response.hasBody()).isFalse();

        assertThat(Database.findUserById("kim")).isEqualTo(new User("kim", "1234", "김주영", "a@naver.com"));
    }

    @Test
    @DisplayName("POST /user/login 요청 시 body로 보낸 데이터로 로그인이 되어야함")
    void loginSuccess() {
        //login 테스트를 위해 유저 추가
        Database.addUser(new User("kim", "1234", "김주영", "a@naver.com"));

        Map<String, String> body = Map.of(
                USERID.getFiled(), "kim",
                PASSWORD.getFiled(), "1234"
        );

        HttpRequest request = new HttpRequest(HTTPMethods.POST, "/user/login", body, ContentType.URL_ENCODED);

        HttpResponse response = new HttpResponse();
        userProcessor.login(request, response);

        assertThat(response.hasCookie()).isTrue();
        assertThat(response.getHeader()).contains("Set-Cookie");
        assertThat(response.getHeader()).contains(ResponseStatus.REDIRECT.getCode());
        
        assertThat(session.getSessionSize()).isEqualTo(1);
    }

}
