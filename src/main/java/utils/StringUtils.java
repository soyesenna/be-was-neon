package utils;

public class StringUtils {

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String END_OF_HTTP_LINE = "\r\n";
    public static final String DEFAULT_URL = "http://localhost:8080";

    private StringUtils() {

    }

    public static String appendNewLine(String string) {
        StringBuilder sb = new StringBuilder();
        return sb.append(string).append(NEWLINE).toString();
    }

    public static String appendHttpEndLine(String string) {
        StringBuilder sb = new StringBuilder();
        return sb.append(string).append(END_OF_HTTP_LINE).toString();
    }
}
