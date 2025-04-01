package exercisetracker.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;


@Entity
@Table(name = "exercise_set")
public class Set extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseCopy exerciseCopy;

    private int reps;
    private Double weight;

    Set() {}

    public Set(int reps, Double weight, ExerciseCopy exerciseCopy) {
        this.reps = reps;
        this.weight = weight;
        this.exerciseCopy = exerciseCopy;
    }

    public int getReps() {
        return this.reps;
    }

    public Double getWeight() {
        return this.weight;
    }

    public ExerciseCopy getExerciseCopy() {
        return exerciseCopy;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setExerciseCopy(ExerciseCopy exerciseCopy) {
        this.exerciseCopy = exerciseCopy;
    }
}
