package processors;


import db.Session;
import processors.util.ProcessorUtil;
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

        response.setHeader(ResponseStatus.REDIRECT, ContentType.NONE);
    }

    @PostMapping("/login")
    public void login(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        User userById = Database.findUserById(body.get(USERID.getFiled()));
        //login fail
        if (userById == null || userById.equalToPassword(body.get(PASSWORD.getFiled()))) {
            String loginFailHTML = Paths.STATIC_RESOURCES + "/login/login_fail.html";
            response.setBody(loginFailHTML);
            response.setHeader(ResponseStatus.OK, ContentType.HTML);
        //login success
        } else {
            String userSessionId = UUID.randomUUID().toString();
            //session에 추가
            Session.addSession(userSessionId, userById);
            response.setCookie(userSessionId);
            response.setHeader(ResponseStatus.REDIRECT, ContentType.NONE);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie().get(ProcessorUtil.COOKIE_SESSION_ID);

        //세션에서 유저 정보 삭제
        Session.deleteSessionById(sessionId);

        response.deleteCookie(sessionId);
        response.setBody(Paths.STATIC_RESOURCES + Paths.DEFAULT_FILE);
        response.setHeader(ResponseStatus.OK, ContentType.HTML);
    }

    @GetMapping("/list")
    public void getUserList(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.checkCookieAndSession(request);

        //로그인 되어있지 않을때 로그인페이지로 이동
        if (user == null) {
            response.setBody(Paths.STATIC_RESOURCES + Paths.LOGIN_DIR + Paths.DEFAULT_FILE);
            response.setHeader(ResponseStatus.OK, ContentType.HTML);
        }else {
            response.setLoginListBody(Paths.STATIC_RESOURCES + "/user_list.html");
            response.setHeader(ResponseStatus.OK, ContentType.HTML);
        }
    }
}
