package webserver;

import db.Database;
import request.util.PostRequestURL;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import static model.UserFiled.*;

public class PostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PostProcessor.class);

    private static final PostProcessor instance = new PostProcessor();

    private PostProcessor() {

    }

    public static PostProcessor getInstance() {
        return instance;
    }

    public void process(String url, Map<String, String> body) {
        try {
            PostRequestURL postRequestURL = PostRequestURL.findProcessByUrl(url);

            switch (postRequestURL) {
                case REGISTER -> register(body);
            }
        }catch (NoSuchElementException e) {
            logger.error("잘못된 post 요청입니다");
        }
        logger.info("Post 요청 완료");
    }

    private void register(Map<String, String> body) {
        Database.addUser(
                new User(body.get(USERID.getFiled()),
                        body.get(PASSWORD.getFiled()),
                        body.get(NAME.getFiled()),
                        body.get(EMAIL.getFiled()))
        );
        logger.debug("회원가입 완료 : {}", Database.findUserById(body.get(USERID.getFiled())));
    }
}
