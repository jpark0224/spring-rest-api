package exercisetracker.model;

import java.util.Objects;

import jakarta.persistence.*;

@Entity
public class ExerciseTemplate extends AbstractExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
