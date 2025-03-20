package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class ExerciseCopy {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String primaryMuscleGroup;

    @ManyToOne
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Set> sets = new ArrayList<>();

    public ExerciseCopy() {}

    public ExerciseCopy(ExerciseTemplate template, Log log) {
        this.name = template.getName();
        this.primaryMuscleGroup = template.getPrimaryMuscleGroup();
        this.log = log;
    }

    public void addSet(Set set) {
        sets.add(set);
        set.setExerciseCopy(this);
    }


}
