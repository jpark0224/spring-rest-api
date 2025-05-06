package exercisetracker.steps;

import exercisetracker.model.Log;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LogSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogService logService;

    private final Long validId = 1L;
    private final Long invalidId = 999L;
    private Log createdLog;
    private ResponseEntity<String> responseEntity;

    @Given("a log exists in the repository")
    public void a_log_exists() {
        Log log = new Log();
        log.setId(validId);
        createdLog = logService.createLog(log);
    }

    @When("the log is completed")
    public void the_log_is_completed() {
        String url = String.format("/logs/%d/complete", validId);
        responseEntity = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
    }
    @Then("the log should have an end time")
    public void the_log_should_have_an_end_time() {
        Log log = logRepository.findById(validId)
                .orElseThrow(() -> new AssertionError("Expected log not found"));
        assertThat(log.getEndTime()).isNotNull();
    }

    @And("a response entity should be returned")
    public void a_response_entity_should_be_returned() {
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}