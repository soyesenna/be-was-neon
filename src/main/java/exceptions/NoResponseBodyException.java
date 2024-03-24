package exceptions;

public class NoResponseBodyException extends Exception{


    public NoResponseBodyException() {
        super("Response Body가 설정되어 있지 않습니다");
    }

    public NoResponseBodyException(String message) {
        super(message);
    }
}
