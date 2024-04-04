package model;

import java.util.Objects;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String profileImgPath = "";

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean equalToPassword(String password) {
        return password.contentEquals(this.password);
    }

    public void setProfileImage(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasProfileImage() {
        return !this.profileImgPath.isEmpty();
    }

    public String getProfileImgPath() {
        return this.profileImgPath;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            return this.userId.equals(user.userId) && this.password.equals(user.password) && this.email.equals(user.email);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, password, email);
    }
}
