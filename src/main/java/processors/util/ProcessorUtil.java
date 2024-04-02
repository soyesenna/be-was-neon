package processors.util;

import db.Database;
import db.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.FileProcessor;
import request.data.HttpRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ProcessorUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtil.class);
    public static final String COOKIE_SESSION_ID = "sid";
    public static final String COOKIE_FEED_NUM = "feedId";
    public static final String LOGIN_PAGE = "/user/login";
    public static final String WELCOME_USER_NAME = "환영합니다❗  %s  님";
    public static final int NO_FEED_COOKIE = -1;

    private ProcessorUtil() {

    }

    //Processor들에서 사용하는 메서드
    //요청으로 들어온 sid쿠키가 유효한지, 세션에 존재하는지 검사한다
    public static User getUserByCookieInSession(HttpRequest request) {
        if (request.getCookie().isEmpty()) return null;
        if (!request.getCookie().containsKey(COOKIE_SESSION_ID)) return null;

        //쿠키를 파싱하고 세션에 등록된경우 User 반환
        String sessionId = request.getCookie().get(COOKIE_SESSION_ID);
        logger.debug(sessionId);
        try {
            User userBySessionId = Session.getUserBySessionId(sessionId);
            User userByIdInDB = Database.findUserById(userBySessionId.getUserId());

            logger.debug("세션과 db에서 유저 찾음");

            return userBySessionId.equals(userByIdInDB) ? userBySessionId : null;
        }catch (IndexOutOfBoundsException | NullPointerException e) {
            logger.error("잘못된 쿠키 입니다");
            return null;
        }
    }

    public static int getFeedNumByCookieInSession(HttpRequest request) {
        if (request.getCookie().isEmpty()) return NO_FEED_COOKIE;
        if (!request.getCookie().containsKey(COOKIE_FEED_NUM)) return NO_FEED_COOKIE;

        String feedId = request.getCookie().get(COOKIE_FEED_NUM);
        int feedNum = NO_FEED_COOKIE;
        try {
            feedNum = Integer.parseInt(feedId);
        } catch (NumberFormatException e) {
            logger.error("잘못된 쿠키입니다");
        }

        return feedNum;
    }

    public static String storeImage(User user, String image, String imageType, String dir, boolean duplicateSave) {
        String hash = String.valueOf(user.hashCode());
        byte[] decodedImage = Base64.getDecoder().decode(image);

        String imageStoredPath = "";
        //이미지를 여러장 저장하는 경우
        if (duplicateSave) {
            String dirPath = dir + "/" + hash;
            File imageDir = new File(dirPath);
            int imageCount = 0;
            //이미 해당 유저의 이미지 디렉터리가 존재하면 이미지 개수 셈
            if (imageDir.exists() && imageDir.isDirectory()) {
                String[] files = imageDir.list();
                for (String fileName : files) {
                    File file = new File(imageDir + File.separator + fileName);
                    if (file.isFile()) {
                        imageCount++;
                    }
                }
            } else {
                imageDir.mkdirs();
            }

            imageStoredPath = dirPath + "/" + imageCount + "." + imageType;
        } else {
            //이미지를 한장만 저장하는 경우
            //프로필 이미지는 유저당 무조건 한장이다
            imageStoredPath = dir + "/" + hash + "." + imageType;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(imageStoredPath)) {
            fileOutputStream.write(decodedImage);
        } catch (IOException e) {
            logger.error("이미지를 저장하는데 실패했습니다");
        }

        return imageStoredPath;
    }
}
