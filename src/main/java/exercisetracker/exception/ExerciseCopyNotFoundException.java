package exercisetracker.exception;

public class ExerciseCopyNotFoundException extends RuntimeException {

    public ExerciseCopyNotFoundException(Long id) {
        super("Could not find exercise " + id + " in the log");
    }
}