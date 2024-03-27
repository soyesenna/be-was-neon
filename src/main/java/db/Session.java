package db;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private static Map<String, User> sessionStore = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Session.class);
    public static final String COOKIE_SESSION_ID = "sid";

    public static void addSession(String id, User user) {
        sessionStore.put(id, user);
    }

    public static User getUserBySessionId(String id) {
        logger.debug(sessionStore.toString());
        User user = sessionStore.get(id);
        if (user == null) return null;

        return user;
    }

    public static long getSessionSize() {
        return sessionStore.size();
    }

    public static void deleteSessionById(String id) {
        sessionStore.remove(id);
    }

    public static void clear() {
        sessionStore = new HashMap<>();
    }
}
