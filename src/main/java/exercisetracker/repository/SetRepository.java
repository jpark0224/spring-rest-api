package exercisetracker.repository;

import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetRepository extends JpaRepository<Set, Long>{
    List<Set> findByExerciseOrderByIdAsc(ExerciseCopy exerciseCopy);
}
