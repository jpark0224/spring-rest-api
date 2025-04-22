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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.LocalDateTime;
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
    private SqsClient sqsClient;

    @BeforeEach
    void setup() {
        logRepository = mock(LogRepository.class);
        exerciseTemplateRepository = mock(ExerciseTemplateRepository.class);
        exerciseCopyRepository = mock(ExerciseCopyRepository.class);

        logService = new LogService(logRepository, exerciseTemplateRepository, exerciseCopyRepository, sqsClient);
    }

    @Test
    void testCreateLog_ReturnsSavedLogWithTimestamp_givenLogRequest() {
        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            LocalDateTime mockedTimestamp = LocalDateTime.of(2099, 1, 1, 1, 0);

            mocked.when(LocalDateTime::now).thenReturn(mockedTimestamp);
            when(logRepository.save(any(Log.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Log logRequest = new Log();
            Log result = logService.createLog(logRequest);

            assertThat(result.getTimestamp()).isEqualTo(mockedTimestamp);
        }
    }

    @Test
    void testCompleteLog_throwsException_givenInvalidLogId() {
        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            LocalDateTime mockedTimestamp = LocalDateTime.of(2099, 1, 1, 2, 0);

            mocked.when(LocalDateTime::now).thenReturn(mockedTimestamp);
            when(logRepository.save(any(Log.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Long validLogId = 1L;
            Long invalidLogId = 999L;

            Log mockedLog = new Log();
            mockedLog.setId(validLogId);

            when(logRepository.findById(invalidLogId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> logService.completeLog(invalidLogId)).isInstanceOf(LogNotFoundException.class);
        }
    }

    @Test
    void testCompleteLog_ReturnsSavedLogWithEndTime_givenLogRequest_givenValidLogId() {
        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            LocalDateTime mockedTimestamp = LocalDateTime.of(2099, 1, 1, 2, 0);

            mocked.when(LocalDateTime::now).thenReturn(mockedTimestamp);
            when(logRepository.save(any(Log.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Long logId = 1L;
            Log mockedLog = new Log();
            mockedLog.setId(logId);

            when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));

            Log result = logService.completeLog(logId);

            assertThat(result.getEndTime()).isEqualTo(mockedTimestamp);
        }
    }

    @Test
    void testAddExerciseToLog_throwsException_givenInvalidLogId() {

        Long invalidLogId = 1L;
        Long templateId = 1L;

        ExerciseTemplate mockedTemplate = new ExerciseTemplate();
        mockedTemplate.setId(templateId);
        mockedTemplate.setName("Dumbbell Lunge");
        mockedTemplate.setPrimaryMuscleGroup("Glutes");

        when(exerciseTemplateRepository.findById(templateId)).thenReturn(Optional.of(mockedTemplate));
        when(logRepository.findById(invalidLogId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logService.addExerciseToLog(invalidLogId, templateId)).isInstanceOf(LogNotFoundException.class);
    }

    @Test
    void testAddExerciseToLog_throwsException_givenInvalidTemplateId() {

        Long logId = 1L;
        Long invalidTemplateId = 1L;

        Log mockedLog = new Log();
        mockedLog.setId(logId);

        when(exerciseTemplateRepository.findById(invalidTemplateId)).thenReturn(Optional.empty());
        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));

        assertThatThrownBy(() -> logService.addExerciseToLog(logId, invalidTemplateId)).isInstanceOf(ExerciseTemplateNotFoundException.class);
    }

    @Test
    void testAddExerciseToLog_shouldAddExerciseToLog_givenValidLogIdAndTemplateId() {

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

        Long validLogId = 1L;
        Long invalidLogId = 999L;
        Long exerciseCopyId = 1L;
        Long templateId = 1L;

        ExerciseCopy mockedExerciseCopy = new ExerciseCopy();
        Log mockedLog = new Log();
        mockedLog.setId(validLogId);

        mockedExerciseCopy.setId(exerciseCopyId);
        mockedExerciseCopy.setName("Dumbbell Lunge");
        mockedExerciseCopy.setPrimaryMuscleGroup("Glutes");
        mockedExerciseCopy.setLog(mockedLog);
        mockedExerciseCopy.setTemplateId(templateId);

        when(exerciseCopyRepository.findById(exerciseCopyId)).thenReturn(Optional.of(mockedExerciseCopy));
        when(logRepository.findById(invalidLogId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logService.removeExerciseFromLog(invalidLogId, exerciseCopyId)).isInstanceOf(LogNotFoundException.class);
    }

    @Test
    void testRemoveExerciseFromLog_throwsException_givenInvalidExerciseCopyId() {

        Long logId = 1L;
        Long invalidExerciseCopyId = 1L;

        Log mockedLog = new Log();
        mockedLog.setId(logId);

        when(exerciseCopyRepository.findById(invalidExerciseCopyId)).thenReturn(Optional.empty());
        when(logRepository.findById(logId)).thenReturn(Optional.of(mockedLog));

        assertThatThrownBy(() -> logService.removeExerciseFromLog(logId, invalidExerciseCopyId)).isInstanceOf(ExerciseCopyNotFoundException.class);
    }

    @Test
    void testRemoveExerciseFromLog_shouldRemoveExerciseFromLog_givenValidLogIdAndExerciseCopyId() {

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
    }
}
