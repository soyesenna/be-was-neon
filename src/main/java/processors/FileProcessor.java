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

import java.util.List;

import static utils.Paths.*;

/**
 * static한 파일 요청한 경우 처리해주는 프로세서
 */
@Processor
public class FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    private static final FileProcessor instance = new FileProcessor();
    private static final String QUERY_KEY_PAGE = "page";


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

    @GetMapping("/")
    public void mainPage(HttpRequest request, HttpResponse response) {
        logger.debug("WelcomePage Call");

        User findUser = ProcessorUtil.getUserByCookieInSession(request);
        if (findUser != null) {
            logger.debug("login welcome page");

            response.addAttribute("USER_NAME", findUser.getName());

            List<Feed> feeds = Database.getAllFeeds();

            //아무 피드도 없을 경우 no_feed.html 반환
            if (feeds.isEmpty()) {
                response.setBody(TEMPLATE_PATH + "/no_feed.html");
                return;
            }

            int requestFeedNum = getNowFeed(request);
            //요청한 피드를 보여준다
            setFeed(response, feeds, requestFeedNum);

            //동적으로 href들 설정
            setDynamicHref(response, requestFeedNum, feeds.size());

            response.setStatus200OK();
            response.setBody(TEMPLATE_PATH + "/main" + DEFAULT_FILE);
        } else {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
            logger.debug("No login welcome page");
        }
    }

    private void setDynamicHref(HttpResponse response, int requestFeedNum, int feedCount) {
        //다음 페이지 href 설정
        //현재 페이지가 마지막 페이지일경우 변경하지 않음
        if (requestFeedNum < feedCount - 1) {
            addPageHref(response, "NEXT_PAGE", requestFeedNum + 1);
        }
        //이전 페이지 href 설정
        //현재 페이지가 0일 경우는 변경하지 않음
        if (requestFeedNum > 0) {
            addPageHref(response, "PREV_PAGE", requestFeedNum - 1);
        }
        //댓글 작성 href 설정
        //댓글은 피드에 속하므로 현재 피드 정보를 주어야함
        String commentHref = "/feed/comment?feed=" + requestFeedNum;
        response.addAttribute("COMMENT_BY_FEED", commentHref);
    }

    private int getNowFeed(HttpRequest request) {
        int requestFeedNum = 0;
        String pageQuery = request.getQueryValue(QUERY_KEY_PAGE);
        if (pageQuery != null) {
            try {
                requestFeedNum = Integer.parseInt(pageQuery);
            }catch (NumberFormatException e) {
                //url 쿼리는 유저 마음대로 바꿔서 들어올 수 있으므로 예외처리함
                requestFeedNum = 0;
            }
        }
        return requestFeedNum;
    }

    private void setFeed(HttpResponse response, List<Feed> feeds, int requestFeedNum) {
        response.addAttribute("POST_USER_NAME", feeds.get(requestFeedNum).getUploaderName());
        response.addAttribute("FEED_IMG", feeds.get(requestFeedNum).getImagePath());
        response.addAttribute("CONTENT", feeds.get(requestFeedNum).getContents());
        response.addAttribute("COMMENT", feeds.get(requestFeedNum).getComments());
    }

    private void addPageHref(HttpResponse response, String name, int page) {
        String prevPageQuery = "/?page=" + page;
        response.addAttribute(name, prevPageQuery);
    }


}
