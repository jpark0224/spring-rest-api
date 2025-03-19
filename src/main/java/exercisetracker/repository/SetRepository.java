package exercisetracker.repository;

import exercisetracker.model.Exercise;
import exercisetracker.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRepository extends JpaRepository<Set, Long>{
    List<Set> findByExerciseOrderByIdAsc(Exercise exercise);
}
