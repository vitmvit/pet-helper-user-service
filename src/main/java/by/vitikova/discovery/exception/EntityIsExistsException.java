package by.vitikova.discovery.exception;

public class EntityIsExistsException extends RuntimeException{

    public EntityIsExistsException() {
    }

    public EntityIsExistsException(String message) {
        super(message);
    }
}