package processors.util;

import db.Database;
import db.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processors.FileProcessor;
import request.data.HttpRequest;

public class ProcessorUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtil.class);
    public static final String COOKIE_SESSION_ID = "sid";

    private ProcessorUtil() {

    }

    //Processor들에서 사용하는 메서드
    //요청으로 들어온 sid쿠키가 유효한지, 세션에 존재하는지 검사한다
    public static User checkCookieAndSession(HttpRequest request) {
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
}
