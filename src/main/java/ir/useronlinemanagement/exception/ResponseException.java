package ir.useronlinemanagement.exception;

public class ResponseException extends RuntimeException{
    private Integer errorCode;
    public ResponseException() {
    }

    public ResponseException(String message,Integer errorCode) {
        super(message);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public ResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
