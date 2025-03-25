package exercisetracker.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
@Table(name = "exercise_set")
public class Set {

    private @Id @GeneratedValue Long id;

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

    public Long getId() {
        return this.id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Set set = (Set) o;
        return Objects.equals(id, set.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Set{" +
                "id=" + id +
                ", exerciseCopy=" + exerciseCopy +
                ", reps=" + reps +
                ", weight=" + weight +
                '}';
    }
}
