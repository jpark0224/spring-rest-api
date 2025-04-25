package exercisetracker.service;

import exercisetracker.model.*;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.PersonalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PersonalRecordService {

    private final PersonalRecordRepository prRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;

    public PersonalRecordService(PersonalRecordRepository prRepository, ExerciseTemplateRepository exerciseTemplateRepository) {
        this.prRepository = prRepository;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
    }

    public void getPersonalRecord(Log completedLog) {
        List<ExerciseCopy> exercises = completedLog.getExerciseCopies();

        for (ExerciseCopy exercise : exercises) {
            List<Set> sets = exercise.getSets();
            Set bestSet = null;
            double orm = 0;

            if (sets != null && !sets.isEmpty()) {
                bestSet = sets.stream()
                        .filter(s -> s.getOneRepMax() != null)
                        .max(Comparator.comparingDouble(Set::getOneRepMax))
                        .orElse(null);
                if (bestSet != null) {
                    orm = bestSet.getOneRepMax();
                }
            }

            Long exerciseTemplateId = exercise.getTemplateId();

            PersonalRecord existingPr = prRepository.findByExerciseTemplateId(exercise.getTemplateId());
            boolean isNewPr = existingPr == null || orm > existingPr.getOneRepMax();

            if (isNewPr && bestSet != null) {
                PersonalRecord pr = (existingPr != null) ? existingPr : new PersonalRecord();

                ExerciseTemplate template = exerciseTemplateRepository.findById(exerciseTemplateId)
                        .orElseThrow(() -> new RuntimeException("ExerciseTemplate not found: " + exerciseTemplateId));

                pr.setExerciseTemplate(template);
                pr.setAchievedAt(completedLog.getEndTime());
                pr.setWeight(bestSet.getWeight());
                pr.setReps(bestSet.getReps());
                pr.setOneRepMax(orm);

                prRepository.save(pr);
            }
        }
    }
}
