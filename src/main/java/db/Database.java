package db;

import feed.Feed;
import model.User;

import java.util.*;

public class Database {

    private Database(){}
    private static Map<String, User> users = new HashMap<>();
    private static Map<User, List<Feed>> usersFeed = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }

    public static void addFeed(User user, Feed feed) {
        if (!usersFeed.containsKey(user)) {
            usersFeed.put(user, new ArrayList<>());
        }
        usersFeed.get(user).add(feed);
    }
}
