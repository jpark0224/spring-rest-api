package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class Exercise {

    private @Id @GeneratedValue Long id;

    private String name;
    private String primaryMuscleGroup;

    Exercise() {}

    public Exercise(String name, String primaryMuscleGroup) {
        this.name = name;
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrimaryMuscleGroup() {
        return this.primaryMuscleGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrimaryMuscleGroup(String primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id) && Objects.equals(name, exercise.name) && Objects.equals(primaryMuscleGroup, exercise.primaryMuscleGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, primaryMuscleGroup);
    }
}
