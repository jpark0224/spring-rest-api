package exercisetracker.dto;

import jakarta.validation.constraints.Min;

public class SetDTO {

    @Min(value = 1, message = "Reps must be at least 1")
    private int reps;

    @Min(value = 0, message = "Weight must be zero or more")
    private Double weight;

    public SetDTO(int reps, Double weight) {

        this.reps = reps;
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
