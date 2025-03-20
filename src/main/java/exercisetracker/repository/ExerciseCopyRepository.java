package exercisetracker.repository;

import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseCopyRepository extends JpaRepository<ExerciseCopy, Long> {
    List<ExerciseCopy> findByLog(Log log);
}