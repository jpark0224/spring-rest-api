package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "exercise_set")
public class Set {

    private @Id @GeneratedValue Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseCopy exerciseCopy;

    private Integer reps;
    private Integer weight;

    Set() {}

    public Set(Integer reps, Integer weight, ExerciseCopy exerciseCopy) {
        this.reps = reps;
        this.weight = weight;
        this.exerciseCopy = exerciseCopy;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getReps() {
        return this.reps;
    }

    public Integer getWeight() {
        return this.weight;
    }

    public ExerciseCopy getExerciseCopy() {
        return exerciseCopy;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public void setWeight(Integer weight) {
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
        return Objects.equals(id, set.id) && Objects.equals(exerciseCopy, set.exerciseCopy) && Objects.equals(reps, set.reps) && Objects.equals(weight, set.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exerciseCopy, reps, weight);
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
