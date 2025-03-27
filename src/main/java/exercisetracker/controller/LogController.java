package exercisetracker.controller;

import exercisetracker.assembler.LogModelAssembler;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.Log;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class LogController {

    private final LogService logService;
    private final LogRepository logRepository;
    private final LogModelAssembler assembler;

    public LogController(LogService logService, LogRepository logRepository, LogModelAssembler assembler) {

        this.logService = logService;
        this.logRepository = logRepository;
        this.assembler = assembler;
    }

    @GetMapping("/logs")
    public CollectionModel<EntityModel<Log>> all() {

        List<EntityModel<Log>> logs = logRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                logs,
                linkTo(methodOn(LogController.class).all()).withSelfRel()
        );
    }

    @GetMapping("/logs/{id}")
    public EntityModel<Log> one(@PathVariable Long id) {

        Log order = logRepository.findById(id)
                .orElseThrow(() -> new LogNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/logs")
    ResponseEntity<EntityModel<Log>> newLog(@RequestBody Log log) {

        Log newLog = logService.createLog(log);

        return ResponseEntity
                .created(linkTo(methodOn(LogController.class).one(newLog.getId())).toUri())
                .body(assembler.toModel(newLog));
    }

    @PutMapping("/logs/{id}/complete")
    ResponseEntity<?> completeLog(@PathVariable Long id) {

        Log completedLog = logService.completeLog(id);

        return ResponseEntity
                .created(linkTo(methodOn(LogController.class).one(completedLog.getId())).toUri())
                .body(assembler.toModel(completedLog));
    }

    @DeleteMapping("/logs/{id}")
    ResponseEntity<?> deleteLog(@PathVariable Long id) {
        if (!logRepository.existsById(id)) {
            throw new LogNotFoundException(id);
        }

        logRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
