package processors;


import db.Session;
import exceptions.NoResponseBodyException;
import property.annotations.GetMapping;
import property.annotations.PostMapping;
import property.annotations.Processor;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.ResponseStatus;
import utils.ContentType;
import utils.Paths;

import java.util.Map;
import java.util.UUID;

import static model.UserFiled.*;

@Processor("/user")
public class UserProcessor {
    private static final Logger logger = LoggerFactory.getLogger(UserProcessor.class);

    private static final UserProcessor instance = new UserProcessor();

    private UserProcessor() {

    }

    public static UserProcessor getInstance() {
        return instance;
    }

    @PostMapping("/create")
    public void register(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        Database.addUser(
                new User(body.get(USERID.getFiled()),
                        body.get(PASSWORD.getFiled()),
                        body.get(NAME.getFiled()),
                        body.get(EMAIL.getFiled()))
        );
        logger.debug("회원가입 완료 : {}", Database.findUserById(body.get(USERID.getFiled())));

        try {
            response.setHeader(ResponseStatus.REDIRECT, ContentType.NONE);
        } catch (NoResponseBodyException e) {
            e.getMessage();
        }
    }

    @PostMapping("/login")
    public void login(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        User userById = Database.findUserById(body.get(USERID.getFiled()));
        //login fail
        if (userById == null || !userById.getPassword().equals(body.get(PASSWORD.getFiled()))) {
            String loginFailHTML = Paths.STATIC_RESOURCES + "/login/login_fail.html";
            response.setBody(loginFailHTML);
            try {
                response.setHeader(ResponseStatus.OK, ContentType.HTML);
            } catch (NoResponseBodyException e) {
                logger.error(e.getMessage());
            }
            //login success
        } else {
            String userSessionId = UUID.randomUUID().toString();
            //session에 추가
            Session.addSession(userSessionId, userById);
            response.setCookie(userSessionId);
            try {
                response.setHeader(ResponseStatus.REDIRECT, ContentType.NONE);
            } catch (NoResponseBodyException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @GetMapping("/logout")
    public void logout(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie().get(Session.COOKIE_SESSION_ID);

        //세션에서 유저 정보 삭제
        Session.deleteSessionById(sessionId);

        try {
            response.deleteCookie(sessionId);
            response.setBody(Paths.STATIC_RESOURCES + Paths.DEFAULT_FILE);
            response.setHeader(ResponseStatus.OK, ContentType.HTML);
        } catch (NoResponseBodyException e) {
            logger.error(e.getMessage());
        }
    }
}
