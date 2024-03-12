package utils;

public class StringUtils {

    public static final String NEWLINE = System.getProperty("line.separator");

    private StringUtils() {

    }

    public static String appendNewLine(String string) {
        StringBuilder sb = new StringBuilder();
        return sb.append(string).append(NEWLINE).toString();
    }
}
