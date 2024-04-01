package db;

import feed.Feed;
import model.User;

import java.util.*;

public class Database {

    private Database(){}
    private static Map<String, User> users = new HashMap<>();
    private static List<Feed> usersFeed = new ArrayList<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }

    public static void addFeed(Feed feed) {
        usersFeed.add(feed);
    }

    public static List<Feed> getAllFeeds() {
        return Collections.unmodifiableList(usersFeed);
    }

    public static boolean isIdExist(String id) {
        for (User user : users.values()) {
            if (user.getUserId().equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    public static boolean isNameExist(String Name) {
        for (User user : users.values()) {
            if (user.getName().equalsIgnoreCase(Name)) return true;
        }
        return false;
    }
}
