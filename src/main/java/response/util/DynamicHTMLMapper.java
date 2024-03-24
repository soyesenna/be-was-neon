package response.util;

public enum DynamicHTMLMapper {

    DYNAMIC_LOGIN_CODE("<!-- DynamicLogin -->"),
    ADD_USERNAME_CODE("<!-- USER_NAME -->"),
    WELCOME_PAGE_LOGIN("<a class=\"btn btn_contained btn_size_s\" href=\"/user/logout\">로그아웃</a>");

    private String value;

    DynamicHTMLMapper(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
