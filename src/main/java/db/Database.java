package db;

import feed.Feed;
import model.User;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    public static User findUserByName(String userName) {
        Optional<User> findUser = users.values().stream()
                .filter(user ->
                    URLDecoder.decode(user.getName(), StandardCharsets.UTF_8).equals(URLDecoder.decode(userName, StandardCharsets.UTF_8)))
                .findFirst();
        return findUser.get();
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

    public static Map<Integer, Feed> getSpecificUserFeeds(User user) {
        Map<Integer, Feed> feedAndIndex = new HashMap<>();
        usersFeed.stream()
                        .filter(feed -> feed.isUploader(user))
                .forEach(feed -> feedAndIndex.put(usersFeed.indexOf(feed), feed));
        return feedAndIndex;
    }

    public static boolean isIdExist(String id) {
        for (User user : users.values()) {
            if (user.getUserId().equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    public static boolean isNameExist(String Name) {
        for (User user : users.values()) {
            if (URLDecoder.decode(user.getName(), StandardCharsets.UTF_8).equals(URLDecoder.decode(Name, StandardCharsets.UTF_8)))
                return true;
        }
        return false;
    }
}
