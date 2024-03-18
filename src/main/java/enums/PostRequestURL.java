package enums;

import java.util.Arrays;

public enum PostRequestURL {
    REGISTER("/user/create");

    private String url;

    PostRequestURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static PostRequestURL findProcessByUrl(String requestUrl) {
        return Arrays.stream(PostRequestURL.values())
                .filter((processes) -> processes.url.equals(requestUrl))
                .findFirst().orElseThrow();
    }
}
