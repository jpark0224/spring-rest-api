package exercisetracker.exception;

public class LogNotFoundException extends RuntimeException {

    public LogNotFoundException(Long id) {
        super("Could not find log " + id);
    }
}