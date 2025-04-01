package exercisetracker.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

@MappedSuperclass
public abstract class AbstractExercise extends BaseEntity {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Primary muscle group is required")
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
