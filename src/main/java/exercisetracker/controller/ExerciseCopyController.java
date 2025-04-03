package exercisetracker.controller;

import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.repository.ExerciseCopyRepository;
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
@RequestMapping("/logs/{logId}/exercises/")
public class ExerciseCopyController {

    private final LogService logService;
    private final ExerciseCopyRepository exerciseCopyRepository;
    private final ExerciseCopyModelAssembler exerciseCopyModelAssembler;
    private final LogRepository logRepository;

    public ExerciseCopyController(LogService logService, ExerciseCopyRepository exerciseCopyRepository, ExerciseCopyModelAssembler assembler, LogRepository logRepository) {

        this.logService = logService;
        this.exerciseCopyRepository = exerciseCopyRepository;
        this.exerciseCopyModelAssembler = assembler;
        this.logRepository = logRepository;
    }

    @GetMapping
    public CollectionModel<EntityModel<ExerciseCopy>> getAllExerciseCopiesInLog(@PathVariable Long logId) {
        if (!logRepository.existsById(logId)) {
            throw new LogNotFoundException(logId);
        }

        List<EntityModel<ExerciseCopy>> exercises = exerciseCopyRepository.findByLogId(logId).stream()
                .map(exerciseCopyModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseCopyController.class).getAllExerciseCopiesInLog(logId)).withSelfRel());
    }

    @GetMapping("/{exerciseCopyId}")
    public EntityModel<ExerciseCopy> getOneExerciseCopyInLog(@PathVariable Long logId, @PathVariable Long exerciseCopyId) {
        ExerciseCopy exerciseCopy = exerciseCopyRepository.findById(exerciseCopyId)
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));

        if (!exerciseCopy.getLog().getId().equals(logId)) {
            throw new ExerciseCopyNotFoundException(exerciseCopyId);
        }

        return exerciseCopyModelAssembler.toModel(exerciseCopy);
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<?> addExerciseCopyToLog(
            @PathVariable Long logId,
            @PathVariable Long templateId) {

        if (!logRepository.existsById(logId)) {
            throw new LogNotFoundException(logId);
        }

        ExerciseCopy newExercise = logService.addExerciseToLog(logId, templateId);

        return ResponseEntity
                .created(linkTo(methodOn(ExerciseCopyController.class).getOneExerciseCopyInLog(logId, newExercise.getId())).toUri())
                .body(exerciseCopyModelAssembler.toModel(newExercise));
    }

    @DeleteMapping("/{exerciseCopyId}")
    ResponseEntity<?> deleteExerciseCopy(@PathVariable Long logId, @PathVariable Long exerciseCopyId) {
        if (!logRepository.existsById(logId)) {
            throw new LogNotFoundException(logId);
        }

        logService.removeExerciseFromLog(logId, exerciseCopyId);

        return ResponseEntity.noContent().build();
    }
}
