package exercisetracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Log {

    private @Id @GeneratedValue Long id;

    @CreatedDate
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    private LocalDateTime endTime;
    private Duration duration;

    private String name;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseCopy> exerciseCopies = new ArrayList<>();

    Log() {
        this.name = "Awesome Workout";
    }

    public Log(String name) {
        this.name = (name != null && !name.isEmpty()) ? name : "Awesome Workout";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ExerciseCopy> getExercises() {
        return exerciseCopies;
    }

    public void setExercises(List<ExerciseCopy> exerciseCopies) {
        this.exerciseCopies = exerciseCopies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(id, log.id) && Objects.equals(timestamp, log.timestamp) && Objects.equals(endTime, log.endTime) && Objects.equals(duration, log.duration) && Objects.equals(name, log.name) && Objects.equals(exerciseCopies, log.exerciseCopies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, endTime, duration, name, exerciseCopies);
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", name='" + name + '\'' +
                ", exercises=" + exerciseCopies +
                '}';
    }
}
