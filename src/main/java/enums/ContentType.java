package enums;

public enum ContentType {
    NONE(""),
    QUERY("text/html;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    SVG("image/svg+xml"),
    PNG("image/png"),
    ICO("image/x-icon");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
