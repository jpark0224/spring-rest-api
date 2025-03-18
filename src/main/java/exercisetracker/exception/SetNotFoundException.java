package exercisetracker.exception;

public class SetNotFoundException extends RuntimeException {

    public SetNotFoundException(Long id) {
        super("Could not find set " + id);
    }
}