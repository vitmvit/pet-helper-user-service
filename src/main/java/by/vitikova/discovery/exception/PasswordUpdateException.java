package by.vitikova.discovery.exception;

public class PasswordUpdateException extends RuntimeException {

    public PasswordUpdateException() {
        super("Password update error!");
    }

    public PasswordUpdateException(String message) {
        super(message);
    }
}
