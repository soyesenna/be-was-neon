package utils;

public enum ContentType {
    NONE(""),
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    SVG("image/svg+xml"),
    PNG("image/png"),
    ICO("image/x-icon"),
    URL_ENCODED("application/x-www-form-urlencoded"),
    JSON("application/json");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
