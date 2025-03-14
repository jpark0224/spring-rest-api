package exercisetracker;

class ExerciseNotFoundException extends RuntimeException {

    ExerciseNotFoundException(Long id) {
        super("Could not find exercise " + id);
    }
}