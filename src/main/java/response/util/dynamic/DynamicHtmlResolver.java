package response.util.dynamic;

import feed.Comment;
import feed.Feed;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static utils.StringUtils.*;
import static response.util.dynamic.Orders.*;

public class DynamicHtmlResolver {
    private static final Logger logger = LoggerFactory.getLogger(DynamicHtmlResolver.class);
    private static final String NAME_FILED = "NAME";

    private final BufferedReader fileReader;

    private final Map<String, Object> attributes;

    private Map<String, String > dataMapping = new HashMap<>();


    public DynamicHtmlResolver(BufferedReader fileReader, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.fileReader = fileReader;
    }

    public String getProcessedBody() {
        StringBuilder sb = new StringBuilder();

        fileReader.lines()
                .forEach(string -> {
                    if (string.contains(DYNAMIC_IDENTITY)) {
                        //동적으로 생성해야하는 부분을 만나면 인터셉트 후 처리
                        sb.append(appendCRLF(interceptAndChange(string)));
                    } else sb.append(appendCRLF(string));
                });

        return sb.toString();
    }

    //dynamic attributes 형식에 맞게 동적으로 생성함
    private String interceptAndChange(String line) {
        logger.debug("Dynamic intercept");
        //필요없는 부분 잘라냄
        line = line.replace(DYNAMIC_IDENTITY, "").replace("<!--", "").replace("-->", "").trim();
        logger.debug(line);
        //order와 data분리
        String[] tmp = line.split(":");
        String order = tmp[0];
        String data = tmp[1];

        //데이터 파싱
        parseData(data);

        //동적으로 html 생성
        String result = "";

        try {
            //동적 생성 order 확인
            if (order.contentEquals(ORDER_INSERT_LIST)) {
                logger.debug("INSERT LIST PROCESS");
                result = insertUserList();
            } else if (order.contentEquals(ORDER_INSERT)) {
                logger.debug("INSERT PROCESS");
                result = insert();
            } else if (order.contentEquals(ORDER_INSERT_ERROR)) {
                logger.debug("INSERT ERROR PROCESS");
                result = insertError();
            } else if (order.contentEquals(ORDER_INSERT_FEED_IMG)) {
                logger.debug("INSERT FEED IMG PROCESS");
                result = insertFeedImg();
            } else if (order.contentEquals(ORDER_REPLACE_HREF)) {
                logger.debug("REPLACE HREF PROCESS");
                result = replaceHref();
            } else if (order.contentEquals(ORDER_INSERT_ARTICLE)) {
                logger.debug("INSERT ARTICLE PROCESS");
                result = insertArticle();
            } else if (order.contentEquals(ORDER_INSERT_COMMENT)) {
                logger.debug("INSERT COMMENT PROCESS");
                result = insertComment();
            } else if (order.contentEquals(ORDER_REPLACE_IMG_SRC)) {
                logger.debug("REPLACE IMG SRC PROCESS");
                result = replaceImgSrc();
            } else if (order.contentEquals(ORDER_INSERT_USER_FEEDS)) {
                logger.debug("INSERT USER FEEDS PROCESS");
                result = insertUserFeeds();
            } else if (order.contentEquals(ORDER_INSERT_SETTING_BTN)) {
                logger.debug("INSERT SETTING BTN PROCESS");
                result = insertSettingBtn();
            } else if (order.contentEquals(ORDER_REPLACE_JS_VAR)) {
                logger.debug("REPLACE JS VAR PROCESS");
                result = replaceJSVar();
            }
        } catch (NoSuchMethodException | InvocationTargetException |IllegalAccessException | IOException e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    private void parseData(String data) {
        StringTokenizer st = new StringTokenizer(data, " ");

        while (st.hasMoreTokens()) {
            String now = st.nextToken();
            logger.debug(now);
            String[] tmp = now.split("=");
            dataMapping.put(tmp[0], tmp[1]);
        }
    }

    private String replaceJSVar() throws IOException{
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";
        String replaceValue = (String) roughData;

        String replaceLine = fileReader.readLine();
        StringTokenizer st = new StringTokenizer(replaceLine, "=");

        StringBuilder html = new StringBuilder();
        html.append(st.nextToken()).append("=");

        html.append("\'").append(replaceValue).append("\';");

        return html.toString();
    }

    private String insertSettingBtn() {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        StringBuilder html = new StringBuilder();
        html.append("<a class=\"btn btn_ghost btn_size_s\" href=\"/user/setting\">")
                .append("<img class=\"post__account__img\" src=\"/img/settingImg.png\"/>")
                .append("</a>");
        return html.toString();
    }

    private String insertUserFeeds() {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        Map<Integer, Feed> feeds = new HashMap<>();

        //동적으로 생성해야하는 타입과 주어진 타입이 일치하는지 검사
        if (roughData instanceof Map) {
            Map<Object, Object> convetMap = (Map<Object, Object>) roughData;
            for (Object rough : convetMap.keySet()) {
                feeds.put((Integer) rough, (Feed) convetMap.get(rough));
            }
        } else throw new IllegalArgumentException("INSERT-USER-FEEDS 명령은 Map 타입만 가질 수 있습니다");

        StringBuilder html = new StringBuilder();
        for (Integer feedIdx : feeds.keySet()) {
            logger.debug(feeds.get(feedIdx).getImagePath());
            html.append("<div class=\"img-wrapper\">");
            html.append("<a href=\"/?page=").append(feedIdx).append("\">");
            html.append("<img src=\"").append(feeds.get(feedIdx).getImagePath()).append("\"></a></div>");
        }
        return html.toString();
    }

    private String replaceImgSrc() throws IOException {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        String replaceLine = fileReader.readLine();

        String src = (String) roughData;
        StringBuilder html = new StringBuilder();

        html.append("<img class=\"post__account__img\" src=\"")
                .append(src)
                .append("\" />");
        return html.toString();
    }

    private String insertComment() {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        List<Comment> comments = new ArrayList<>();

        //동적으로 생성해야하는 타입과 주어진 타입이 일치하는지 검사
        if (roughData instanceof List) {
            List<Object> convetList = (List<Object>) roughData;
            for (Object rough : convetList) {
                comments.add((Comment) rough);
            }
        } else throw new IllegalArgumentException("INSERT-COMMENT 명령은 List 타입만 가질 수 있습니다");

        StringBuilder html = new StringBuilder();

        for (Comment comment : comments) {
            String userId = URLDecoder.decode(comment.getWriteUserName(), StandardCharsets.UTF_8);
            String content = URLDecoder.decode(comment.getComment(), StandardCharsets.UTF_8);
            html.append("<li class=\"comment__item\">")
                    .append("<div class=\"comment__item__user\">");
            if (comment.writeUserHasProfile()) {
                html.append("<img class=\"comment__item__user__img\" src=\"")
                        .append(comment.writeUserProfileImg())
                        .append("\" />");
            }else html.append("<img class=\"comment__item__user__img\" />");
            html.append("<p class=\"comment__item__user__nickname\">").append(userId).append("</p>")
            .append("</div>")
            .append("<p class=\"comment__item__article\">")
            .append(content).append("</p>").append("</li>");
            }
        return html.toString();
    }




    private String insertArticle() {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        String contents = (String) roughData;

        StringBuilder html = new StringBuilder();
        html.append("<p class=\"post__article\">");

        contents = contents.replace("\\n", CRLF);
        html.append(contents);
        html.append("</p>").append("</div>");

        return html.toString();
    }

    private String replaceHref() throws IOException {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";
        String hrefPath = (String) roughData;

        String replaceLine = fileReader.readLine();
        StringTokenizer st = new StringTokenizer(replaceLine, " ");

        StringBuilder html = new StringBuilder();
        while (st.hasMoreTokens()) {
            String now = st.nextToken();
            if (now.contains("href")) {
                html.append("href=\"").append(hrefPath).append("\"");
            } else html.append(now);
            html.append(" ");
        }

        if (html.charAt(html.length() - 1) != '>') html.append(">");

        return html.toString();
    }

    private String insertFeedImg() {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        String imgPath = (String) roughData;
        StringBuilder html = new StringBuilder();

        html.append("<img class=\"post__img\" src=\"").append(imgPath).append("\" />");

        return html.toString();
    }

    /**
     * Error를 만들어주는 메서드
     * 빨간색 글씨를 만들어준다
     * @return
     */
    private String insertError() {
        Object roughAttribute = attributes.get(dataMapping.get(NAME_FILED));
        if (roughAttribute == null) return "";

        StringBuilder html = new StringBuilder();
        String errorMessage = (String) roughAttribute;
        html.append("<p class=\"error-message\">").append(errorMessage).append("</p>");


        return html.toString();
    }


    /**
     * user list html을 만들어주는 메서드
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private String insertUserList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object roughAttribute = attributes.get(dataMapping.get(NAME_FILED));
        if (roughAttribute == null) return "";

        StringBuilder html = new StringBuilder();

        List<User> users = new ArrayList<>();

        //동적으로 생성해야하는 타입과 주어진 타입이 일치하는지 검사
        if (roughAttribute instanceof List) {
        List<Object> convetList = (List<Object>) roughAttribute;
            for (Object rough : convetList) {
                users.add((User) rough);
            }
        } else throw new IllegalArgumentException("INSERT-LIST 명령은 List 타입만 가질 수 있습니다");

        //설정된 sequence에 따라 html에 추가
        List<String> insertSequence = new ArrayList<>();
        StringTokenizer seqToken = new StringTokenizer(dataMapping.get("SEQ"), ", ()");
        while (seqToken.hasMoreTokens()) {
            insertSequence.add(seqToken.nextToken());
        }

        for (User user : users) {
            html.append("<tr>");
            for (String seq : insertSequence) {
                html.append("<th>");
                Class<User> userClass = User.class;
                Method getMethod = userClass.getMethod("get" + seq);
                html.append(getMethod.invoke(user));
                html.append("</th>");
            }
            html.append("</tr>");
        }
        return html.toString();
    }

    private String insert() throws UnsupportedEncodingException {
        Object roughData = attributes.get(dataMapping.get(NAME_FILED));
        if (roughData == null) return "";

        String insetString = "";

        //String attibutes 처리
        insetString = (String) roughData;
        insetString = URLDecoder.decode(insetString, StandardCharsets.UTF_8);

        return insetString;
    }

}
