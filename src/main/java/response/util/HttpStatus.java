package response.util;

public enum HttpStatus {
    OK("HTTP/1.1 200 OK "),
    REDIRECT("HTTP/1.1 302 Found "),
    NOT_FOUND("HTTP/1.1 404 Not Found ");

    private String code;

    HttpStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
