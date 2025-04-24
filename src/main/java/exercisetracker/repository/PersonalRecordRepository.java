package exercisetracker.repository;

import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {
    List<ExerciseCopy> findByExerciseTemplateId(Long ExerciseTemplateId);
}