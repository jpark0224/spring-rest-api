package exercisetracker.model;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractExercise extends BaseEntity {

    private String name;
    private String primaryMuscleGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryMuscleGroup() {
        return primaryMuscleGroup;
    }

    public void setPrimaryMuscleGroup(String primaryMuscleGroup) {
        this.primaryMuscleGroup = primaryMuscleGroup;
    }
}
