package processors;

import db.Database;
import db.Session;
import exceptions.NoResponseBodyException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import property.annotations.GetMapping;
import property.annotations.Processor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.DynamicHTMLMapper;
import response.util.ResponseStatus;
import utils.Paths;

import static utils.Paths.*;

/**
 * static한 파일 요청한 경우 처리해주는 프로세서
 */
@Processor
public class FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    private static final FileProcessor instance = new FileProcessor();

    private FileProcessor() {

    }

    public static FileProcessor getInstance() {
        return instance;
    }

    @GetMapping(".")
    public void responseFile(HttpRequest request, HttpResponse response) {
        logger.debug("ResponseFile Call");
        String path = Paths.STATIC_RESOURCES + request.getURL();

        setResponse(request, response, path);
    }

    @GetMapping("/login")
    public void loginPage(HttpRequest request, HttpResponse response) {
        logger.debug("LoginPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        setResponse(request, response, filePath);
    }

    @GetMapping("/")
    public void welcomePage(HttpRequest request, HttpResponse response) {
        logger.debug("WelcomePage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE.substring(1);

        User checkSession = checkCookieAndSession(request);
        if (checkSession != null) {
            logger.debug("login welcome page");
            setResponse(request, response, filePath, DynamicHTMLMapper.WELCOME_PAGE_LOGIN);
        } else {
            logger.debug("No login welcome page");
            setResponse(request, response, filePath);
        }
    }

    @GetMapping("/registration")
    public void registerPage(HttpRequest request, HttpResponse response) {
        logger.debug("RegisterPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        setResponse(request, response, filePath);
    }

    private void setResponse(HttpRequest request, HttpResponse response, String filePath, DynamicHTMLMapper dynamicIdentity) {
        response.setBody(filePath, dynamicIdentity);
        try {
            response.setHeader(ResponseStatus.OK, request.getContentType());
        } catch (NoResponseBodyException e) {
            logger.error(e.getMessage());
        }
    }

    private void setResponse(HttpRequest request, HttpResponse response, String filePath) {
        response.setBody(filePath);
        try {
            response.setHeader(ResponseStatus.OK, request.getContentType());
        } catch (NoResponseBodyException e) {
            logger.error(e.getMessage());
        }
    }

    private User checkCookieAndSession(HttpRequest request) {
        if (request.getCookie().isEmpty()) return null;
        if (!request.getCookie().containsKey(Session.COOKIE_SESSION_ID)) return null;

        //쿠키를 파싱하고 세션에 등록된경우 User 반환
        String sessionId = request.getCookie().get(Session.COOKIE_SESSION_ID);
        logger.debug(sessionId);
        try {
            User userBySessionId = Session.getUserBySessionId(sessionId);
            logger.debug("세션에서 유저 찾음");
            User userByIdInDB = Database.findUserById(userBySessionId.getUserId());
            logger.debug("db에서 유저 찾음");

            return userBySessionId.equals(userByIdInDB) ? userBySessionId : null;
        }catch (IndexOutOfBoundsException | NullPointerException e) {
            logger.error("잘못된 쿠키 입니다");
            return null;
        }
    }
}
