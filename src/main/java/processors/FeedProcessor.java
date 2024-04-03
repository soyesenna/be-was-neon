package processors;

import db.Database;
import feed.Comment;
import feed.Feed;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.util.ProcessorUtil;
import property.annotations.GetMapping;
import property.annotations.PostMapping;
import property.annotations.Processor;
import property.annotations.ResponseStatus;
import request.data.HttpRequest;
import response.data.HttpResponse;
import response.util.HttpStatus;
import utils.ContentType;

import java.util.List;
import java.util.Map;

import static utils.Paths.*;


@Processor("/feed")
public class FeedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FeedProcessor.class);
    private static final FeedProcessor instance = new FeedProcessor();
    private static final String QUERY_KEY_FEED = "feed";


    private FeedProcessor() {

    }

    public static FeedProcessor getInstance() {
        return instance;
    }

    @GetMapping("")
    public void articlePostPage(HttpRequest request, HttpResponse response) {
        logger.debug("Postpage Call");
        User user = ProcessorUtil.getUserByCookieInSession(request);

        if (user == null) {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
        } else {
            if (user.hasProfileImage()) {
                response.addAttribute("USER_PROFILE", user.getProfileImgPath());
            }
            response.addAttribute("USER_NAME", user.getName());
            response.setBody(TEMPLATE_PATH + request.getURL() + DEFAULT_FILE);
        }
    }

    @PostMapping("/write")
    @ResponseStatus(HttpStatus.OK)
    public void feedWrite(HttpRequest request, HttpResponse response) {
        logger.debug("FeedWrite Call");
        User writeUser = ProcessorUtil.getUserByCookieInSession(request);

        if (writeUser == null) {
            response.setJsonBody("redirectUrl", ProcessorUtil.LOGIN_PAGE);
            return;
        } else {
            Map<String, String> body = request.getBody();
            String contents = body.get("contents");
            if (contents.equals("NONE")) {
                response.setBody(TEMPLATE_PATH + "/feed" + DEFAULT_FILE);
                return;
            }
            String fileData = body.get("file");
            if (fileData.equals("NONE")) {
                response.setBody(TEMPLATE_PATH + "/feed" + DEFAULT_FILE);
                return;
            }
            ContentType fileType = ContentType.valueOf(body.get("file_type").toUpperCase());

            String imageStoredpath = ProcessorUtil.storeImage(writeUser, fileData, body.get("file_type"), FEED_IMAGE_DIR, true);

            Feed feed = new Feed(writeUser, imageStoredpath, fileType, body.get("contents"));
            Database.addFeed(feed);
        }
        response.setJsonBody("redirectUrl", "/");

    }



    @GetMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public void commentPage(HttpRequest request, HttpResponse response) {
        logger.debug("Commnet page Call");
        User user = ProcessorUtil.getUserByCookieInSession(request);

        if (user == null) {
            response.setBody(ProcessorUtil.LOGIN_PAGE);
        }else {
            int feedNum = -1;
            List<Feed> feeds = Database.getAllFeeds();
            try {
                //쿼리가 잘 들어왔는지 검사
                feedNum = Integer.parseInt(request.getQueryValue(QUERY_KEY_FEED));
                feeds.get(feedNum);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                logger.error("잘못된 쿼리 형식입니다");
                return;
            }
            response.addAttribute("USER_NAME", user.getName());
            //feedId라는 쿠키를 저장하여 피드를 구분함
            response.setCookie("feedId", String.valueOf(feedNum));
            response.setBody(STATIC_RESOURCES + "/comment" + DEFAULT_FILE);
        }
    }

    @PostMapping("/comment")
    public void commentWrite(HttpRequest request, HttpResponse response) {
        User user = ProcessorUtil.getUserByCookieInSession(request);
        int feedNum = ProcessorUtil.getFeedNumByCookieInSession(request);

        if (user == null) {
            response.setStatus302Found(ProcessorUtil.LOGIN_PAGE);
            return;
        }
        if (feedNum == ProcessorUtil.NO_FEED_COOKIE) {
            response.setStatus302Found("/");
        } else {
            //댓글이 달릴 피드를 가져옴
            List<Feed> feeds = Database.getAllFeeds();
            Feed nowFeed = feeds.get(feedNum);

            Map<String, String> body = request.getBody();
            String comment = body.get("comment");

            nowFeed.addComment(new Comment(user, comment));

            response.setStatus302Found("/");
        }

        //댓글 작성이 완료되었으므로 feedId쿠키 삭제
        response.deleteCookie("feedId", String.valueOf(feedNum));
    }
}
