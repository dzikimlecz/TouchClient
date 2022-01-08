package me.dzikimlecz.touchclient.mainview;

public class ResponseException extends RuntimeException {
    public int getStatusCode() {
        return statusCode;
    }

    private final int statusCode;

    public ResponseException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public ResponseException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
