package exercisetracker.exception;

public class ExerciseTemplateNotFoundException extends RuntimeException {

    public ExerciseTemplateNotFoundException(Long id) {
        super("Could not find exercise template " + id);
    }
}