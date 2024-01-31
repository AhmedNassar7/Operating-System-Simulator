package os;

public class OSException extends Exception {
    public OSException(String message) {
        super(message);
    }

    public OSException(String message, Throwable cause) {
        super(message, cause);
    }
}
