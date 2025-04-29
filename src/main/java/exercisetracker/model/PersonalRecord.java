package exercisetracker.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "exercise_template_id", nullable = false)
    private ExerciseTemplate exerciseTemplate;
    private LocalDateTime achievedAt;
    private int reps;
    private Double weight;
    private Double oneRepMax;

    public PersonalRecord() {}

    public PersonalRecord(ExerciseTemplate exerciseTemplate, LocalDateTime achievedAt, int reps, Double weight, Double oneRepMax) {
        this.exerciseTemplate = exerciseTemplate;
        this.achievedAt = achievedAt;
        this.reps = reps;
        this.weight = weight;
        this.oneRepMax = oneRepMax;
    }

    public ExerciseTemplate getExerciseTemplate() {
        return exerciseTemplate;
    }

    public void setExerciseTemplate(ExerciseTemplate exerciseTemplate) {
        this.exerciseTemplate = exerciseTemplate;
    }

    public LocalDateTime getAchievedAt() {
        return achievedAt;
    }

    public void setAchievedAt(LocalDateTime achievedAt) {
        this.achievedAt = achievedAt;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getOneRepMax() {
        return oneRepMax;
    }

    public void setOneRepMax(Double oneRepMax) {
        this.oneRepMax = oneRepMax;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
