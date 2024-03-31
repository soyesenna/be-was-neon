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
import java.util.Map;
import java.util.UUID;

public class UserProcessorTest {

    private UserProcessor userProcessor;

    @BeforeEach
    void before() throws Exception {
        userProcessor = UserProcessor.getInstance();
        Session.clear();
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

        assertThat(response.getHeader()).contains(HttpStatus.REDIRECT.getCode());
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
        assertThat(response.getHeader()).contains(HttpStatus.REDIRECT.getCode());

        assertThat(Session.getSessionSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("로그아웃시 세션에서 유저 정보 삭제")
    void logout() {
        User user = new User("kim", "1234", "김주영", "a@naver.com");
        //db에 추가
        Database.addUser(user);

        //세션에 추가
        String uuid = UUID.randomUUID().toString();
        Session.addSession(uuid, user);

        //추가됐는지 확인
        assertThat(Session.getSessionSize()).isEqualTo(1);

        HttpRequest request = new HttpRequest(HTTPMethods.GET, "/user/logout", ContentType.HTML);
        //삭제할 쿠키정보 추가
        request.addCookie("sid=" + uuid + "; max-age=0");
        HttpResponse response = new HttpResponse();

        userProcessor.logout(request, response);

        //세션에서 삭제됐는지 확인
        assertThat(Session.getSessionSize()).isEqualTo(0);
        //삭제된 아이디로 가져오면 null반환
        assertThat(Session.getUserBySessionId(uuid)).isNull();
    }
}
