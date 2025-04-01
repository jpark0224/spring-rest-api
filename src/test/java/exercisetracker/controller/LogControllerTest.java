package exercisetracker.controller;

import exercisetracker.assembler.LogModelAssembler;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.Log;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @Mock
    private LogModelAssembler assembler;

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogController logController;

    @Test
    void testGetAllLogs_returnsCollectionModelWithLogs() {
        Log log1 = new Log();
        log1.setId(1L);

        Log log2 = new Log();
        log2.setId(2L);

        List<Log> logs = List.of(log1, log2);

        EntityModel<Log> logModel1 = EntityModel.of(log1);
        logModel1.add(Link.of("/logs/1").withSelfRel());

        EntityModel<Log> logModel2 = EntityModel.of(log2);
        logModel2.add(Link.of("/logs/2").withSelfRel());

        when(logRepository.findAll()).thenReturn(logs);
        when(assembler.toModel(log1)).thenReturn(logModel1);
        when(assembler.toModel(log2)).thenReturn(logModel2);

        CollectionModel<EntityModel<Log>> result = logController.getAllLogs();

        assertThat(result.getContent()).containsExactlyInAnyOrder(logModel1, logModel2);
        assertThat(result.getLinks())
                .anyMatch(link -> link.getRel().value().equals("self"));
        result.getContent().forEach(model ->
                assertThat(model.getLinks())
                        .anyMatch(link -> link.getRel().value().equals("self"))
        );
    }

    @Test
    void testGetOneLog_returnsEntityModelWithLog_givenValidLogId() {
        Log log = new Log();
        Long logId = 1L;
        log.setId(logId);

        EntityModel<Log> logModel = EntityModel.of(log);
        logModel.add(Link.of("/logs/1").withSelfRel());

        when(logRepository.findById(logId)).thenReturn(Optional.of(log));
        when(assembler.toModel(log)).thenReturn(logModel);

        EntityModel<Log> result = logController.getOneLog(logId);

        assertThat(result.getContent()).isEqualTo(log);
        assertThat(result.getLinks())
                .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals("/logs/1"));
    }

    @Test
    void testGetOneLog_throwsException_givenInvalidLogId() {

        Long validLogId = 1L;
        Long invalidLogId = 999L;

        Log mockedLog = new Log();
        mockedLog.setId(validLogId);

        when(logRepository.findById(invalidLogId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> logController.getOneLog(invalidLogId)).isInstanceOf(LogNotFoundException.class);
    }

    @Test
    void testPostLog_returnsCreatedResponseWithLocationHeaderAndBody() {
        Log requestLog = new Log();

        LocalDateTime timestamp = LocalDateTime.of(2099, 1, 1, 1, 0);

        Log createdLog = new Log();
        createdLog.setId(1L);
        createdLog.setTimestamp(timestamp);

        EntityModel<Log> logModel = EntityModel.of(createdLog);
        logModel.add(Link.of("/logs/1").withSelfRel());

        when(logService.createLog(requestLog)).thenReturn(createdLog);
        when(assembler.toModel(createdLog)).thenReturn(logModel);

        ResponseEntity<EntityModel<Log>> response = logController.postLog(requestLog);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo("/logs/1");
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).isEqualTo(createdLog);
        assertThat(response.getBody().getLinks())
                .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals("/logs/1"));
    }

    @Test
    void testCompleteLog_returnsCreatedResponseWithLocationHeaderAndBody() {
        Log requestLog = new Log();

        LocalDateTime timestamp = LocalDateTime.of(2099, 1, 1, 1, 0);
        Long logId = 1L;

        Log completedLog = new Log();
        completedLog.setId(logId);
        completedLog.setEndTime(timestamp);

        EntityModel<Log> logModel = EntityModel.of(completedLog);
        logModel.add(Link.of("/logs/1").withSelfRel());

        when(logService.completeLog(logId)).thenReturn(completedLog);
        when(assembler.toModel(completedLog)).thenReturn(logModel);

        ResponseEntity<EntityModel<Log>> response = logController.completeLog(logId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo("/logs/1");
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).isEqualTo(completedLog);
        assertThat(response.getBody().getLinks())
                .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals("/logs/1"));
    }

    @Test
    void testDeleteLog_throwsException_givenInvalidLogId() {
        Long logId = 99L;

        when(logRepository.existsById(logId)).thenReturn(false);

        assertThatThrownBy(() -> logController.deleteLog(logId))
                .isInstanceOf(LogNotFoundException.class)
                .hasMessageContaining("Could not find log");
    }

    @Test
    void testDeleteLog_deletesLogAndReturnsNoContent_givenValidLogId() {
        Long logId = 1L;

        when(logRepository.existsById(logId)).thenReturn(true);

        ResponseEntity<EntityModel<Log>> response = logController.deleteLog(logId);

        verify(logRepository).deleteById(logId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
