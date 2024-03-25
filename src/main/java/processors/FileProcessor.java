package processors;

import exceptions.NoResponseBodyException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.util.ProcessorUtil;
import property.annotations.GetMapping;
import property.annotations.Processor;
import request.data.HttpRequest;
import response.data.HttpResponse;
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

        User checkSession = ProcessorUtil.checkCookieAndSession(request);
        if (checkSession != null) {
            logger.debug("login welcome page");
            setResponse(request, response, filePath, checkSession.getName());
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

    private void setResponse(HttpRequest request, HttpResponse response, String filePath, String userId) {
        response.setBodyInLogin(filePath, userId);
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


}
