package exercisetracker.repository;

import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseCopyRepository extends JpaRepository<ExerciseCopy, Long> {
    List<ExerciseCopy> findByLog(Log log);
}