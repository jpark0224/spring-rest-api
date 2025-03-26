package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class ExerciseTemplate {

    private @Id @GeneratedValue Long id;

    private String name;
    private String primaryMuscleGroup;

    public ExerciseTemplate() {}

    public ExerciseTemplate(String name, String primaryMuscleGroup) {
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

    public void setId(Long id) {
        this.id = id;
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
        ExerciseTemplate that = (ExerciseTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ExerciseTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryMuscleGroup='" + primaryMuscleGroup + '\'' +
                '}';
    }
}
