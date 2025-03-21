package exercisetracker.controller;

import exercisetracker.assembler.LogModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.LogRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LogController {

    private final LogRepository logRepository;
    private final ExerciseCopyRepository exerciseCopyRepository;
    private final LogModelAssembler assembler;

    LogController(LogRepository logRepository, LogModelAssembler assembler, ExerciseCopyRepository exerciseCopyRepository) {

        this.logRepository = logRepository;
        this.assembler = assembler;
        this.exerciseCopyRepository = exerciseCopyRepository;
    }

    @GetMapping("/logs")
    public CollectionModel<EntityModel<Log>> all() {

        List<EntityModel<Log>> logs = logRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(logs,
                linkTo(methodOn(LogController.class).all()).withSelfRel());
    }

    @GetMapping("/logs/{id}")
    public EntityModel<Log> one(@PathVariable Long id) {

        Log order = logRepository.findById(id) //
                .orElseThrow(() -> new LogNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/logs")
    ResponseEntity<EntityModel<Log>> newLog(@RequestBody Log log) {

        log.setTimestamp(LocalDateTime.now());
        Log newLog = logRepository.save(log);

        return ResponseEntity
                .created(linkTo(methodOn(LogController.class).one(newLog.getId())).toUri())
                .body(assembler.toModel(newLog));
    }

    @PutMapping("/logs/{id}")
    ResponseEntity<?> replaceLog(@RequestBody Log logRequest, @PathVariable Long id) {

    }

//    @PutMapping("/logs/{id}")
//    ResponseEntity<?> replaceLog(@RequestBody Log logRequest, @PathVariable Long id) {
//
//        Log newLog = createLog(logRequest);
//
//        Log updatedLog = logRepository.findById(id)
//                .map(log -> {
//                    log.logReps(newLog.getReps());
//                    log.logWeight(newLog.getWeight());
//                    log.logExerciseCopy(newLog.getExerciseCopy());
//                    return logRepository.save(log);
//                })
//                .orElseGet(() -> {
//                    return logRepository.save(newLog);
//                });
//
//        EntityModel<Log> entityModel = assembler.toModel(updatedLog);
//
//        return ResponseEntity
//                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
//                .body(entityModel);
//    }

    @DeleteMapping("/logs/{id}")
    ResponseEntity<?> deleteLog(@PathVariable Long id) {

        logRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
