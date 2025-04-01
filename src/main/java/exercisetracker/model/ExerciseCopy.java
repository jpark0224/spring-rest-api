package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
public class ExerciseCopy extends AbstractExercise {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "log_id", nullable = false)
    private Log log;

    @OneToMany(mappedBy = "exerciseCopy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Set> sets = new ArrayList<>();

    private Long templateId;

    public ExerciseCopy() {}

    public ExerciseCopy(ExerciseTemplate template, Log log) {
        this.setName(template.getName());
        this.setPrimaryMuscleGroup(template.getPrimaryMuscleGroup());
        this.log = log;
        this.templateId = template.getId();
    }

    public void addSet(Set set) {
        sets.add(set);
        set.setExerciseCopy(this);
    }

    public void removeSet(Set set) {
        sets.remove(set);
        set.setExerciseCopy(null);
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Log getLog() {
        return log;
    }

    @JsonIgnore
    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
}
