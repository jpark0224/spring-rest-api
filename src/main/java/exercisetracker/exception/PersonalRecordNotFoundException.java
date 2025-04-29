package exercisetracker.exception;

public class PersonalRecordNotFoundException extends RuntimeException {

    public PersonalRecordNotFoundException(Long id) {
        super("Could not find personal record " + id);
    }
}