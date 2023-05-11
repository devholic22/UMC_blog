package umc.blog.exception;

public class TargetNotFoundException extends RuntimeException {
    public TargetNotFoundException() {
    }

    public TargetNotFoundException(String message) {
        super(message);
    }
}
