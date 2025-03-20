package exercisetracker.service;

import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.LogRepository;
import exercisetracker.repository.SetRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final ExerciseCopyRepository exerciseCopyRepository;
    private final SetRepository setRepository;

    public LogService(LogRepository logRepository,
                      ExerciseTemplateRepository exerciseTemplateRepository,
                      ExerciseCopyRepository exerciseCopyRepository,
                      SetRepository setRepository) {
        this.logRepository = logRepository;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
        this.exerciseCopyRepository = exerciseCopyRepository;
        this.setRepository = setRepository;
    }

    private Log findLogById(Long logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new LogNotFoundException(logId));
    }

    private ExerciseCopy findExerciseCopyById(Long exerciseCopyId) {
        return exerciseCopyRepository.findById(exerciseCopyId)
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));
    }
}
