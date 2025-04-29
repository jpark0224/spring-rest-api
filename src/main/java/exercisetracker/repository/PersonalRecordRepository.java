package exercisetracker.repository;

import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {
    PersonalRecord findByExerciseTemplateId(Long ExerciseTemplateId);
}