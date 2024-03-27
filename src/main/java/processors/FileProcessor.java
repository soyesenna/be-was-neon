package processors;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.util.ProcessorUtil;
import property.annotations.GetMapping;
import property.annotations.Processor;
import property.annotations.Status;
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
    @Status(ResponseStatus.OK)
    public void responseFile(HttpRequest request, HttpResponse response) {
        logger.debug("ResponseFile Call");
        String path = Paths.STATIC_RESOURCES + request.getURL();

        response.setBody(path);
    }

    @GetMapping("/login")
    @Status(ResponseStatus.OK)
    public void loginPage(HttpRequest request, HttpResponse response) {
        logger.debug("LoginPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
    }

    @GetMapping("/")
    @Status(ResponseStatus.OK)
    public void welcomePage(HttpRequest request, HttpResponse response) {
        logger.debug("WelcomePage Call");

        User checkSession = ProcessorUtil.checkCookieAndSession(request);
        if (checkSession != null) {
            logger.debug("login welcome page");

            response.addAttribute("USER_NAME", checkSession.getName());
            response.setBody(TEMPLATE_PATH + "/main" + DEFAULT_FILE);

        } else {
            response.setBody(STATIC_RESOURCES + DEFAULT_FILE);
            logger.debug("No login welcome page");
        }
    }

    @GetMapping("/registration")
    @Status(ResponseStatus.OK)
    public void registerPage(HttpRequest request, HttpResponse response) {
        logger.debug("RegisterPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
    }

}
