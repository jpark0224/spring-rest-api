package exercisetracker.service;

import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.PersonalRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonalRecordService {

    private final PersonalRecordRepository prRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;

    public PersonalRecordService(PersonalRecordRepository prRepository, ExerciseTemplateRepository exerciseTemplateRepository) {
        this.prRepository = prRepository;
        this.exerciseTemplateRepository = exerciseTemplateRepository;
    }

    public void createPersonalRecord(Long logId) {

    }

}
