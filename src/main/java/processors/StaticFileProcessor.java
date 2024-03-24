package processors;

import exceptions.NoResponseBodyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import property.annotations.GetMapping;
import property.annotations.Processor;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.ResponseStatus;
import utils.ContentType;
import utils.Paths;

/**
 * static한 파일 요청한 경우 처리해주는 프로세서
 */
@Processor
public class StaticFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileProcessor.class);
    private static final StaticFileProcessor instance = new StaticFileProcessor();
    private static final String DEFAULT_FILE = "/index.html";

    private StaticFileProcessor() {

    }

    public static StaticFileProcessor getInstance() {
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
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + "index.html";

        setResponse(request, response, filePath);
    }

    @GetMapping("/registration")
    public void registerPage(HttpRequest request, HttpResponse response) {
        logger.debug("RegisterPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        setResponse(request, response, filePath);
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
