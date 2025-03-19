package exercisetracker.model;

public class Status {
    private int statusCode;
    private String message;

    public Status(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
