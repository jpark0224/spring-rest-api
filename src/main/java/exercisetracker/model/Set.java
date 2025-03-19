package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;


@Entity
@Table(name = "exercise_set")
public class Set {

    private @Id @GeneratedValue Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private Integer reps;
    private Integer weight;

    Set() {}

    public Set(Integer reps, Integer weight, Exercise exercise) {
        this.reps = reps;
        this.weight = weight;
        this.exercise = exercise;
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

    public Exercise getExercise() {
        return exercise;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Set set = (Set) o;
        return Objects.equals(id, set.id) && Objects.equals(exercise, set.exercise) && Objects.equals(reps, set.reps) && Objects.equals(weight, set.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exercise, reps, weight);
    }

    @Override
    public String toString() {
        return "Set{" +
                "id=" + id +
                ", exercise=" + exercise +
                ", reps=" + reps +
                ", weight=" + weight +
                '}';
    }
}
