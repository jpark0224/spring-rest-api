package exercisetracker.dto;

public class SetDTO {
    private Integer reps;
    private Integer weight;
    private Long exerciseCopyId;

    public SetDTO(Integer reps, Integer weight, Long exerciseCopyId) {
        this.reps = reps;
        this.weight = weight;
        this.exerciseCopyId = exerciseCopyId;
    }

    public Integer getReps() {
        return reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public Long getExerciseCopyId() {
        return exerciseCopyId;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setExerciseCopyId(Long exerciseCopyId) {
        this.exerciseCopyId = exerciseCopyId;
    }
}
