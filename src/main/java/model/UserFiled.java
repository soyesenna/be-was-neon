package model;

import java.util.List;

public enum UserFiled {
    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private String filed;

    UserFiled(String filed) {
        this.filed = filed;
    }

    public String getFiled() {
        return filed;
    }

}
