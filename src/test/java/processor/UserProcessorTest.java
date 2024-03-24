package processor;

import static model.UserFiled.*;
import static org.assertj.core.api.Assertions.*;

import db.Database;
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

    @BeforeEach
    void before() {
        userProcessor = UserProcessor.getInstance();
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
}
