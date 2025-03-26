package exercisetracker.service;

import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    void testAddExerciseToLog_throwsException_givenInvalidLogId() {

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

    @Test
    void testAddExerciseToLog_throwsException_givenInvalidTemplateId() {

        Long logId = 1L;
        Long templateId = 1L;

        Log mockedLog = new Log();
        mockedLog.setId(logId);

        when(exerciseTemplateRepository.findById(templateId)).thenReturn(Optional.empty());
        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));

        assertThatThrownBy(() -> logService.addExerciseToLog(logId, templateId)).isInstanceOf(ExerciseTemplateNotFoundException.class);
    }

    @Test
    void testAddExerciseToLog_throwsException_givenValidLogIdAndTemplateId() {

        Long logId = 1L;
        Long templateId = 1L;

        Log mockedLog = new Log();
        mockedLog.setId(logId);

        ExerciseTemplate mockedTemplate = new ExerciseTemplate();
        mockedTemplate.setId(templateId);
        mockedTemplate.setName("Dumbbell Lunge");
        mockedTemplate.setPrimaryMuscleGroup("Glutes");

        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));
        when(exerciseTemplateRepository.findById(templateId)).thenReturn(Optional.of(mockedTemplate));
        when(exerciseCopyRepository.save(any(ExerciseCopy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExerciseCopy result = logService.addExerciseToLog(logId, templateId);

        assertThat(result.getName()).isEqualTo(mockedTemplate.getName());
        assertThat(result.getPrimaryMuscleGroup()).isEqualTo(mockedTemplate.getPrimaryMuscleGroup());
        assertThat(result.getLog()).isEqualTo(mockedLog);
    }

    @Test
    void testRemoveExerciseFromLog_throwsException_givenInvalidLogId() {

        Long logId = 1L;
        Long exerciseCopyId = 1L;
        Long templateId = 1L;

        ExerciseCopy mockedExerciseCopy = new ExerciseCopy();
        Log mockedLog = new Log();
        mockedExerciseCopy.setId(exerciseCopyId);
        mockedExerciseCopy.setName("Dumbbell Lunge");
        mockedExerciseCopy.setPrimaryMuscleGroup("Glutes");
        mockedExerciseCopy.setLog(mockedLog);
        mockedExerciseCopy.setTemplateId(templateId);

        when(exerciseCopyRepository.findById(exerciseCopyId)).thenReturn(Optional.of(mockedExerciseCopy));
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logService.removeExerciseFromLog(logId, exerciseCopyId)).isInstanceOf(LogNotFoundException.class);
    }

    @Test
    void testRemoveExerciseFromLog_throwsException_givenInvalidExerciseCopyId() {

        Long logId = 1L;
        Long exerciseCopyId = 1L;

        Log mockedLog = new Log();
        mockedLog.setId(logId);

        when(exerciseCopyRepository.findById(exerciseCopyId)).thenReturn(Optional.empty());
        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));

        assertThatThrownBy(() -> logService.removeExerciseFromLog(logId, exerciseCopyId)).isInstanceOf(ExerciseCopyNotFoundException.class);
    }

    @Test
    void testRemoveExerciseFromLog_throwsException_givenValidLogIdAndExerciseCopyId() {

        Long logId = 1L;
        Long exerciseCopyId = 1L;
        Long templateId = 1L;

        ExerciseCopy mockedExerciseCopy = new ExerciseCopy();
        Log mockedLog = new Log();
        mockedLog.setId(logId);
        mockedExerciseCopy.setId(exerciseCopyId);
        mockedExerciseCopy.setName("Dumbbell Lunge");
        mockedExerciseCopy.setPrimaryMuscleGroup("Glutes");
        mockedExerciseCopy.setTemplateId(templateId);
        mockedLog.addExerciseCopy(mockedExerciseCopy);

        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));
        when(exerciseCopyRepository.findById(exerciseCopyId)).thenReturn(Optional.of(mockedExerciseCopy));

        logService.removeExerciseFromLog(logId, exerciseCopyId);

        Collection<ExerciseCopy> copies = mockedLog.getExerciseCopies();
        assertThat(copies).doesNotContain(mockedExerciseCopy);
        verify(logRepository).save(mockedLog);
    }
}
