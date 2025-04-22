package exercisetracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import exercisetracker.assembler.LogModelAssembler;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.Log;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import software.amazon.awssdk.services.sqs.SqsClient;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class LogController {

    private final LogService logService;
    private final LogRepository logRepository;
    private final LogModelAssembler assembler;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public LogController(LogService logService, LogRepository logRepository, LogModelAssembler assembler, SqsClient sqsClient, ObjectMapper objectMapper) {

        this.logService = logService;
        this.logRepository = logRepository;
        this.assembler = assembler;
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/logs")
    public CollectionModel<EntityModel<Log>> getAllLogs() {

        List<EntityModel<Log>> logs = logRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                logs,
                linkTo(methodOn(LogController.class).getAllLogs()).withSelfRel()
        );
    }

    @GetMapping("/logs/{id}")
    public EntityModel<Log> getOneLog(@PathVariable Long id) {

        Log logs = logRepository.findById(id)
                .orElseThrow(() -> new LogNotFoundException(id));

        return assembler.toModel(logs);
    }

    @PostMapping("/logs")
    ResponseEntity<EntityModel<Log>> postLog(@RequestBody Log log) {

        Log newLog = logService.createLog(log);

        return ResponseEntity
                .created(linkTo(methodOn(LogController.class).getOneLog(newLog.getId())).toUri())
                .body(assembler.toModel(newLog));
    }

    @PutMapping("/logs/{id}/complete")
    public ResponseEntity<EntityModel<Log>> completeLog(@PathVariable Long id) {
        Log completedLog = logService.completeLog(id);

        try {
            String payload = objectMapper.writeValueAsString(completedLog);
            logService.sendLog(payload);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        EntityModel<Log> logModel = assembler.toModel(completedLog);

        return ResponseEntity
                .created(linkTo(methodOn(LogController.class).getOneLog(completedLog.getId())).toUri())
                .body(logModel);
    }

    @DeleteMapping("/logs/{id}")
    ResponseEntity<EntityModel<Log>> deleteLog(@PathVariable Long id) {
        if (!logRepository.existsById(id)) {
            throw new LogNotFoundException(id);
        }

        logRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
