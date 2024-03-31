package processors;

import db.Database;
import feed.Feed;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.util.ProcessorUtil;
import property.annotations.GetMapping;
import property.annotations.Processor;
import property.annotations.ResponseStatus;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.HttpStatus;
import utils.Paths;

import java.util.ArrayList;
import java.util.List;

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
    @ResponseStatus(HttpStatus.OK)
    public void responseFile(HttpRequest request, HttpResponse response) {
        logger.debug("ResponseFile Call");
        if (request.getURL().contains("/feed_image")) response.setBody("." + request.getURL());
        else response.setBody(STATIC_RESOURCES + request.getURL());
    }

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void loginPage(HttpRequest request, HttpResponse response) {
        logger.debug("LoginPage Call");
        String filePath = TEMPLATE_PATH + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void welcomePage(HttpRequest request, HttpResponse response) {
        logger.debug("WelcomePage Call");

        User findUser = ProcessorUtil.getUserByCookieInSession(request);
        if (findUser != null) {
            logger.debug("login welcome page");

            response.addAttribute("USER_NAME", findUser.getName());

            List<Feed> feeds = Database.getAllFeeds();

            //아무 피드도 없을 경우
            if (feeds.size() == 0) {
                response.setBody(STATIC_RESOURCES + "/no_feed.html");
                return;
            }

            //우선 첫번째 피드의 사진과 글을 보여준다
            //나중에 쿼리 또는 body로 들어온 페이지의 피드를 보여주도록 수정
            response.addAttribute("POST_USER_NAME", feeds.get(0).getUploaderName());
//            String imagePath = String.valueOf(findUser.hashCode()) + ""
            response.addAttribute("FEED_IMG", feeds.get(0).getImagePath());
            response.setBody(TEMPLATE_PATH + "/main" + DEFAULT_FILE);

        } else {
            response.setBody(TEMPLATE_PATH + "/login" + DEFAULT_FILE);
            logger.debug("No login welcome page");
        }
    }


    @GetMapping("/registration")
    public void registerPage(HttpRequest request, HttpResponse response) {
        logger.debug("RegisterPage Call");
        String filePath = Paths.STATIC_RESOURCES + request.getURL() + DEFAULT_FILE;

        response.setBody(filePath);
    }

    @GetMapping("/feed")
    @ResponseStatus(HttpStatus.OK)
    public void articlePostPage(HttpRequest request, HttpResponse response) {
        logger.debug("Postpage Call");
        User user = ProcessorUtil.getUserByCookieInSession(request);

        if (user == null) {
            response.setBody(TEMPLATE_PATH + "/login" + DEFAULT_FILE);
        } else {
            response.addAttribute("USER_NAME", user.getName());
            response.setBody(TEMPLATE_PATH + request.getURL() + DEFAULT_FILE);
        }
    }

}
