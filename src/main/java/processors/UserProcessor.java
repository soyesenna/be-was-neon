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
import property.annotations.Status;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.ResponseStatus;
import utils.Paths;

import java.util.*;

import static model.UserFiled.*;
import static utils.Paths.*;

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

        response.setStatus302Found(DEFAULT_FILE);
    }

    @PostMapping("/login")
    public void login(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        User userById = Database.findUserById(body.get(USERID.getFiled()));
        //login fail
        if (userById == null || !userById.equalToPassword(body.get(PASSWORD.getFiled()))) {
            String loginFailHTML = Paths.STATIC_RESOURCES + "/login/login_fail.html";
            response.setStatus200OK();
            response.setBody(loginFailHTML);
        //login success
        } else {
            String userSessionId = UUID.randomUUID().toString();
            //session에 추가
            Session.addSession(userSessionId, userById);
            response.addAttribute("USER_NAME", userById.getName());

            response.setStatus302Found("/");
            response.setCookie(userSessionId);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie().get(ProcessorUtil.COOKIE_SESSION_ID);

        //세션에서 유저 정보 삭제
        Session.deleteSessionById(sessionId);

        response.setStatus302Found("/");
        response.deleteCookie(sessionId);
    }

    @GetMapping("/list")
    @Status(ResponseStatus.OK)
    public void getUserList(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.getUserByCookieInSession(request);

        //로그인 되어있지 않을때 로그인페이지로 이동
        if (user == null) {
            response.setBody(Paths.STATIC_RESOURCES + Paths.LOGIN_DIR + Paths.DEFAULT_FILE);
        }else {
            response.addAttribute("USER_LIST", Database.findAll().stream().toList());
            response.setBody(TEMPLATE_PATH + "/user_list.html");
        }
    }
}
