package exercisetracker.steps;

import exercisetracker.model.*;
import exercisetracker.repository.*;
import exercisetracker.service.LogService;
import io.cucumber.java.Before;
import io.cucumber.java.an.E;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import io.cucumber.spring.ScenarioScope;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
@ScenarioScope
public class LogSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogService logService;

    @Autowired
    private ExerciseTemplateRepository exerciseTemplateRepository;

    @Autowired
    private ExerciseCopyRepository exerciseCopyRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private PersonalRecordRepository personalRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Long invalidId = 999L;
    private final Double oneRepMax = 15.0;
    private Log savedLog;
    private ExerciseTemplate savedTemplate;
    private ResponseEntity<String> responseEntity;

    @Before
    public void resetDatabase() {
        savedLog = null;
        responseEntity = null;
        logRepository.deleteAll();
    }

    @Given("a log exists in the repository")
    public void a_log_exists() {
        Log log = new Log();
        log.setTimestamp(LocalDateTime.now());
        savedLog = logRepository.save(log);
    }

    @When("the log is completed")
    public void the_log_is_completed() {
        String url = String.format("/logs/%d/complete", savedLog.getId());
        responseEntity = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
    }

    @Then("the log should have an end time")
    public void the_log_should_have_an_end_time() {
        Log log = logRepository.findById(savedLog.getId())
                .orElseThrow(() -> new AssertionError("Expected log not found"));
        assertThat(log.getEndTime()).isNotNull();
    }

    @And("a response entity should be returned")
    public void a_response_entity_should_be_returned() {
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @And("the log has sets with new personal records")
    public void the_log_has_sets_with_new_personal_records() {
        ExerciseTemplate template = new ExerciseTemplate("barbell squat", "hamstrings");
        savedTemplate = exerciseTemplateRepository.save(template);
        ExerciseCopy exerciseCopy = new ExerciseCopy(savedTemplate, savedLog);
        ExerciseCopy savedCopy = exerciseCopyRepository.save(exerciseCopy);
        Set set = new Set(12, 10.0, oneRepMax, savedCopy);
        Set savedSet = setRepository.save(set);
        savedCopy.addSet(savedSet);
        savedLog.addExerciseCopy(savedCopy);
    }

    @Then("the personal records should be saved to the database")
    public void the_personal_records_should_be_saved_to_the_database() {
        PersonalRecord personalRecord = personalRecordRepository.findByExerciseTemplateId(savedTemplate.getId());
        assertThat(personalRecord).isNotNull();
        assertThat(personalRecord.getOneRepMax()).isEqualTo(oneRepMax);
    }
}