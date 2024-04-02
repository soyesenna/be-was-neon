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
import property.annotations.ResponseStatus;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.HttpStatus;
import utils.Paths;

import java.io.ByteArrayOutputStream;
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

    @GetMapping("/registration")
    public void registerPage(HttpRequest request, HttpResponse response) {
        logger.debug("RegisterPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void loginPage(HttpRequest request, HttpResponse response) {
        logger.debug("LoginPage Call");
        String filePath = TEMPLATE_PATH + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
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

        response.setStatus302Found("/");
    }

    @PostMapping("/login")
    public void login(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();

        User userById = Database.findUserById(body.get(USERID.getFiled()));
        //login fail
        if (userById == null || !userById.equalToPassword(body.get(PASSWORD.getFiled()))) {
            String loginFailHTML = TEMPLATE_PATH + ProcessorUtil.LOGIN_PAGE + DEFAULT_FILE;

            response.addAttribute("NO_LOGIN", "존재하지 않는 아이디 또는 비밀번호 입니다");
            response.setStatus200OK();
            response.setBody(loginFailHTML);
        //login success
        } else {
            String userSessionId = UUID.randomUUID().toString();
            //session에 추가
            Session.addSession(userSessionId, userById);
            response.addAttribute("USER_NAME", String.format(ProcessorUtil.WELCOME_USER_NAME, userById.getName()));

            response.setStatus302Found("/");
            response.setSidCookie(userSessionId);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie().get(ProcessorUtil.COOKIE_SESSION_ID);

        //세션에서 유저 정보 삭제
        Session.deleteSessionById(sessionId);

        response.setStatus302Found("/");
        response.deleteSidCookie(sessionId);
    }

    @PostMapping("/checkId")
    public void checkIdIsDuplicate(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        String userId = body.get("userId");

        if (userId == null) {
            logger.error("JSON 요청이 잘못되었습니다");
            return;
        }

        if (userId.equalsIgnoreCase("NONE") || Database.isIdExist(userId)) {
            response.setJsonBody("check", "false");
        } else {
            response.setJsonBody("check", "true");
        }
    }

    @PostMapping("/checkName")
    public void checkNameIsDuplicate(HttpRequest request, HttpResponse response) {
        Map<String, String> body = request.getBody();
        String userName = body.get("userName");

        if (userName == null) {
            logger.error("JSON 요청이 잘못되었습니다");
            return;
        }

        if (userName.equalsIgnoreCase("NONE") || Database.isNameExist(userName)) {
            response.setJsonBody("check", "false");
        } else {
            response.setJsonBody("check", "true");
        }
    }

    @GetMapping("/setting")
    public void profileSettingPage(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.getUserByCookieInSession(request);

        //로그인 되어있지 않을때 로그인페이지로 이동
        if (user == null) {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
        }else {
            response.setStatus200OK();
            response.addAttribute("USER_NAME", String.format(ProcessorUtil.WELCOME_USER_NAME, user.getName()));
            response.setBody(TEMPLATE_PATH + request.getURL() + DEFAULT_FILE);
        }
    }

    @PostMapping("/setting")
    public void changeProfile(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.getUserByCookieInSession(request);

        //로그인 되어있지 않을때 로그인페이지로 이동
        if (user == null) {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
        }else {
            Map<String, String> body = request.getBody();
            String newName = body.get("name");
            String newProfileImg = body.get("file");

            String fileType = body.get("file_type");
            String imgSavePath = ProcessorUtil.storeImage(user, newProfileImg, fileType, PROFILE_IMAGE_DIR, false);

            user.setProfileImage(imgSavePath);
            user.setName(newName);

            response.setStatus200OK();
            response.setJsonBody("redirectUrl", "/");
        }
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public void getUserList(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.getUserByCookieInSession(request);

        //로그인 되어있지 않을때 로그인페이지로 이동
        if (user == null) {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
        } else {
            response.addAttribute("USER_LIST", Database.findAll().stream().toList());
            response.setBody(TEMPLATE_PATH + "/user_list.html");
        }
    }
}
