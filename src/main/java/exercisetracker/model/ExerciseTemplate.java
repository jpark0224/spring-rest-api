package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class ExerciseTemplate extends AbstractExercise {

    @OneToOne(mappedBy = "exerciseTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PersonalRecord personalRecord;

    public ExerciseTemplate() {}

    public ExerciseTemplate(String name, String primaryMuscleGroup) {
        this.setName(name);
        this.setPrimaryMuscleGroup(primaryMuscleGroup);
    }

    public PersonalRecord getPersonalRecord() {
        return personalRecord;
    }

    public void setPersonalRecord(PersonalRecord personalRecord) {
        this.personalRecord = personalRecord;
    }
}
