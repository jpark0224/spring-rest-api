package exercisetracker.controller;

import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.repository.ExerciseCopyRepository;
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

    public ExerciseCopyController(LogService logService, ExerciseCopyRepository exerciseCopyRepository, ExerciseCopyModelAssembler assembler) {

        this.logService = logService;
        this.exerciseCopyRepository = exerciseCopyRepository;
        this.exerciseCopyModelAssembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ExerciseCopy>> getAllExerciseCopiesInLog(@PathVariable Long logId) {
        List<EntityModel<ExerciseCopy>> exercises = exerciseCopyRepository.findByLogId(logId).stream()
                .map(exerciseCopyModelAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseCopyController.class).getAllExerciseCopiesInLog(logId)).withSelfRel());
    }

    @GetMapping("/{exerciseCopyId}")
    public EntityModel<ExerciseCopy> getOneExerciseCopy(@PathVariable Long logId, @PathVariable Long exerciseCopyId) {

        ExerciseCopy exerciseCopy = exerciseCopyRepository.findById(exerciseCopyId) //
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));

        return exerciseCopyModelAssembler.toModel(exerciseCopy);
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<?> addExerciseCopyToLog(
            @PathVariable Long logId,
            @PathVariable Long templateId) {

        ExerciseCopy newExercise = logService.addExerciseToLog(logId, templateId);

        return ResponseEntity
                .created(linkTo(methodOn(ExerciseCopyController.class).getOneExerciseCopy(logId, newExercise.getId())).toUri())
                .body(exerciseCopyModelAssembler.toModel(newExercise));
    }

    @DeleteMapping("/{exerciseCopyId}")
    ResponseEntity<?> deleteExerciseCopy(@PathVariable Long logId, @PathVariable Long exerciseCopyId) {

        logService.removeExerciseFromLog(logId, exerciseCopyId);

        return ResponseEntity.noContent().build();
    }
}
