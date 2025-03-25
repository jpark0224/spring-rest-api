package exercisetracker.dto;

public class SetDTO {
    private Integer reps;
    private Integer weight;

    public SetDTO(Integer reps, Integer weight) {
        this.reps = reps;
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
