package exercisetracker.dto;

public class SetDTO {
    private Integer reps;
    private Integer weight;
    private Long exerciseId;

    public SetDTO(Integer reps, Integer weight, Long exerciseId) {
        this.reps = reps;
        this.weight = weight;
        this.exerciseId = exerciseId;
    }

    public Integer getReps() {
        return reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }
}
