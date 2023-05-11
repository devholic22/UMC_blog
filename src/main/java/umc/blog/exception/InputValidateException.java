package umc.blog.exception;

public class InputValidateException extends RuntimeException {
    public InputValidateException() {
    }

    public InputValidateException(String message) {
        super(message);
    }
}
