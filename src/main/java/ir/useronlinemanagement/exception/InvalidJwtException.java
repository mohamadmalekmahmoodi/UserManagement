package ir.useronlinemanagement.exception;

public class InvalidJwtException extends RuntimeException{
    public InvalidJwtException(final String message, final Exception exception) {
        super(message, exception);
    }
}
