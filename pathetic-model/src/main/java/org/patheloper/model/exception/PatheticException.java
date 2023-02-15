package org.patheloper.model.exception;

public class PatheticException extends RuntimeException {

    public PatheticException(String message) {
        super(message);
    }

    public PatheticException(String message, Throwable cause) {
        super(message, cause);
    }
}
