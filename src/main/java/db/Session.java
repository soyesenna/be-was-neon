package db;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final Map<String, User> sessionStore = new HashMap<>();
    private static final Session instance = new Session();
    private Session() {

    }

    public static Session getInstance() {
        return instance;
    }

    public void addSession(String id, User user) {
        sessionStore.put(id, user);
    }

    public User getUserBySessionId(String id) {
        User user = sessionStore.get(id);
        if (user == null) return null;

        return user;
    }

    public long getSessionSize() {
        return this.sessionStore.size();
    }
}
