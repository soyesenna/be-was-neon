package enums;

public enum ContentType {
    NONE(""),
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    SVG("image/svg+xml"),
    PNG("image/png");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
