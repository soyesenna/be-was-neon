package response.util;

public enum ResponseStatus {
    OK("HTTP/1.1 200 OK "),
    REDIRECT("HTTP/1.1 302 Found ");

    private String code;

    ResponseStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
