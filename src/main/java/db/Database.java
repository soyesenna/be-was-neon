package db;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private Database(){}
    private static Map<String, User> users = new HashMap<>();

    static {
//        users.put("kim", new User("kim", "123", "김주영", "a@naver.com"));
    }

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
