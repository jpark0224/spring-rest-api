package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "exerciseCopy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Set> sets = new ArrayList<>();

    private Long templateId;

    public ExerciseCopy() {}

    public ExerciseCopy(ExerciseTemplate template, Log log) {
        this.name = template.getName();
        this.primaryMuscleGroup = template.getPrimaryMuscleGroup();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Log getLog() {
        return log;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseCopy that = (ExerciseCopy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ExerciseCopy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", primaryMuscleGroup='" + primaryMuscleGroup + '\'' +
                ", log=" + log +
                ", sets=" + sets +
                '}';
    }
}
