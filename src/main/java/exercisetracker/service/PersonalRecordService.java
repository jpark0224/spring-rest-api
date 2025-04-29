package exercisetracker.service;

import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.model.*;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.PersonalRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<PersonalRecord> getPrs(Log completedLog) {
        List<PersonalRecord> updatedPrs = new ArrayList<>();

        List<ExerciseCopy> exercises = completedLog.getExerciseCopies();
        LocalDateTime achievedAt = completedLog.getEndTime();

        if (exercises == null || exercises.isEmpty()) {
            return updatedPrs;
        }

        for (ExerciseCopy exercise : exercises) {
            PersonalRecord pr = processExerciseForPr(exercise, achievedAt);
            if (pr != null) {
                updatedPrs.add(pr);
            }
        }

        return updatedPrs;
    }

    private PersonalRecord processExerciseForPr(ExerciseCopy exercise, LocalDateTime achievedAt) {
        if (exercise.getSets() == null || exercise.getSets().isEmpty()) return null;

        Long templateId = exercise.getTemplateId();
        if (templateId == null) return null;

        Set bestSet = exercise.getSets().stream()
                .filter(s -> s.getOneRepMax() != null)
                .max(Comparator.comparingDouble(Set::getOneRepMax))
                .orElse(null);

        if (bestSet == null) return null;

        double bestOrm = bestSet.getOneRepMax();

        PersonalRecord existingPr = prRepository.findByExerciseTemplateId(templateId);

        ExerciseTemplate template = exerciseTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ExerciseTemplateNotFoundException(templateId));

        PersonalRecord pr = null;

        if (existingPr == null || bestOrm > existingPr.getOneRepMax()) {
            if (existingPr == null) {
                pr = new PersonalRecord();
                pr.setExerciseTemplate(template);
            } else {
                pr = existingPr;
            }

            pr.setAchievedAt(achievedAt);
            pr.setWeight(bestSet.getWeight());
            pr.setReps(bestSet.getReps());
            pr.setOneRepMax(bestOrm);

            prRepository.save(pr);
        }

        return pr;
    }
}
