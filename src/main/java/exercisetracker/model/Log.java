package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    private LocalDateTime endTime;
    private Duration duration;

    private String name;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("exercises")
    @JsonManagedReference
    private List<ExerciseCopy> exerciseCopies = new ArrayList<>();

    public Log() {
        this.name = "Awesome Workout";
    }

    public Log(String name) {
        this.name = (name != null && !name.isEmpty()) ? name : "Awesome Workout";
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.timestamp != null) {
            this.duration = Duration.between(this.timestamp, endTime);
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public Long getDurationInMinutes() {
        return duration != null ? duration.toMinutes() : null;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null && !name.isEmpty()) ? name : "Awesome Workout";
    }

    @JsonIgnore
    public List<ExerciseCopy> getExerciseCopies() {
        return exerciseCopies;
    }

    public void setExerciseCopies(List<ExerciseCopy> exerciseCopies) {
        this.exerciseCopies = exerciseCopies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addExerciseCopy(ExerciseCopy exerciseCopy) {
        exerciseCopies.add(exerciseCopy);
        exerciseCopy.setLog(this);
    }

    public void removeExerciseCopy(ExerciseCopy exerciseCopy) {
        exerciseCopies.remove(exerciseCopy);
        exerciseCopy.setLog(null);
    }
}
