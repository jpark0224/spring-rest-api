package exercisetracker.service;

import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.util.UUID;

@Service
public class LogService {

    private final LogRepository logRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final ExerciseCopyRepository exerciseCopyRepository;
    private final SqsClient sqsClient;

    public LogService(LogRepository logRepository,
                      ExerciseTemplateRepository exerciseTemplateRepository,
                      ExerciseCopyRepository exerciseCopyRepository, SqsClient sqsClient) {
        this.logRepository = logRepository;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
        this.exerciseCopyRepository = exerciseCopyRepository;
        this.sqsClient = sqsClient;
    }

    private Log findLogById(Long logId) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new LogNotFoundException(logId));
    }

    private ExerciseCopy findExerciseCopyById(Long exerciseCopyId) {
        return exerciseCopyRepository.findById(exerciseCopyId)
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));
    }

    private ExerciseTemplate findExerciseTemplateById(Long exerciseTemplateId) {
        return exerciseTemplateRepository.findById(exerciseTemplateId)
                .orElseThrow(() -> new ExerciseTemplateNotFoundException(exerciseTemplateId));
    }

    @Transactional
    public Log createLog(Log logRequest) {
        logRequest.setTimestamp(LocalDateTime.now());
        return logRepository.save(logRequest);
    }

    @Transactional
    public Log completeLog(Long logId) {
        Log log = findLogById(logId);
        log.setEndTime(LocalDateTime.now());
        return logRepository.save(log);
    }

    @Transactional
    public ExerciseCopy addExerciseToLog(Long logId, Long exerciseTemplateId) {
        ExerciseTemplate exerciseTemplate = findExerciseTemplateById(exerciseTemplateId);
        Log log = findLogById(logId);

        ExerciseCopy copiedExercise = new ExerciseCopy(exerciseTemplate, log);
        log.addExerciseCopy(copiedExercise);
        ExerciseCopy savedExerciseCopy = exerciseCopyRepository.save(copiedExercise);
        logRepository.save(log);
        return savedExerciseCopy;
    }

    @Transactional
    public void removeExerciseFromLog(Long logId, Long exerciseCopyId) {
        ExerciseCopy exerciseCopy = findExerciseCopyById(exerciseCopyId);
        Log log = findLogById(logId);

        log.removeExerciseCopy(exerciseCopy);
        logRepository.save(log);
    }

    public void sendLog(String jsonPayload) {
        String queueUrl = "http://localhost:4566/000000000000/generate-workout-summary-queue.fifo";

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(jsonPayload)
                .messageGroupId("summary")
                .messageDeduplicationId(UUID.randomUUID().toString())
                .build();

        sqsClient.sendMessage(request);
    }
}
