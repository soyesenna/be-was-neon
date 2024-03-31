package utils;

public enum ContentType {
    NONE(""),
    HTML("text/html; charset=utf-8"),
    CSS("text/css"),
    TXT("text/plain"),
    SVG("image/svg+xml"),
    PNG("image/png"),
    JPG("image/jpeg"),
    JPEG("image/jpeg"),
    GIF("image/gif"),
    ICO("image/x-icon"),
    JS("application/javascript"),
    URL_ENCODED("application/x-www-form-urlencoded"),
    MULTIPART("multipart/form-data"),
    JSON("application/json");


    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
