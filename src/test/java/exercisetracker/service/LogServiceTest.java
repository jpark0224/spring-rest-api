package exercisetracker.service;

import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogServiceTest {

    private LogRepository logRepository;
    private ExerciseTemplateRepository exerciseTemplateRepository;
    private ExerciseCopyRepository exerciseCopyRepository;
    private LogService logService;

    @BeforeEach
    void setup() {
        logRepository = mock(LogRepository.class);
        exerciseTemplateRepository = mock(ExerciseTemplateRepository.class);
        exerciseCopyRepository = mock(ExerciseCopyRepository.class);

        logService = new LogService(logRepository, exerciseTemplateRepository, exerciseCopyRepository);
    }

    @Test
    void testAddExerciseToLog_throwsException_whenLogNotFound() {

        Long logId = 1L;
        Long templateId = 1L;

        ExerciseTemplate mockedTemplate = new ExerciseTemplate();
        mockedTemplate.setId(templateId);
        mockedTemplate.setName("Dumbbell Lunge");
        mockedTemplate.setPrimaryMuscleGroup("Glutes");

        when(exerciseTemplateRepository.findById(templateId)).thenReturn(Optional.of(mockedTemplate));
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logService.addExerciseToLog(logId, templateId)).isInstanceOf(LogNotFoundException.class);
    }
}
