package exercisetracker.repository;

import exercisetracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTemplateRepository extends JpaRepository<Exercise, Long> {

}