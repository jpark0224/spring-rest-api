package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class ExerciseTemplate extends AbstractExercise {

    public ExerciseTemplate() {}

    public ExerciseTemplate(String name, String primaryMuscleGroup) {
        this.setName(name);
        this.setPrimaryMuscleGroup(primaryMuscleGroup);
    }
}
