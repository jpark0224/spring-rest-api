package exercisetracker.controller;

import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.service.LogService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ExerciseCopyController {

    private final LogService logService;
    private final ExerciseCopyRepository exerciseCopyRepository;
    private final ExerciseCopyModelAssembler assembler;


    ExerciseCopyController(LogService logService, ExerciseCopyRepository exerciseCopyRepository, ExerciseCopyModelAssembler assembler) {

        this.logService = logService;
        this.exerciseCopyRepository = exerciseCopyRepository;
        this.assembler = assembler;
    }

    @GetMapping("/exercises")
    public CollectionModel<EntityModel<ExerciseCopy>> all() {

        List<EntityModel<ExerciseCopy>> exercises = exerciseCopyRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseCopyController.class).all()).withSelfRel());
    }

    @GetMapping("/exercises/{id}")
    public EntityModel<ExerciseCopy> one(@PathVariable Long id) {

        ExerciseCopy exerciseCopy = exerciseCopyRepository.findById(id) //
                .orElseThrow(() -> new ExerciseCopyNotFoundException(id));

        return assembler.toModel(exerciseCopy);
    }

    @PostMapping("/logs/{logId}/exercises/{templateId}")
    public ResponseEntity<?> addExerciseToLog(
            @PathVariable Long logId,
            @PathVariable Long templateId) {

        logService.addExerciseToLog(logId, templateId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/exercises/{id}")
    ResponseEntity<?> replaceExercise(@RequestBody ExerciseCopy newExerciseCopy, @PathVariable Long id) {

        ExerciseCopy updatedExerciseCopy = exerciseCopyRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(newExerciseCopy.getName());
                    exercise.setPrimaryMuscleGroup(newExerciseCopy.getPrimaryMuscleGroup());
                    return exerciseCopyRepository.save(exercise);
                })
                .orElseGet(() -> {
                    return exerciseCopyRepository.save(newExerciseCopy);
                });

        EntityModel<ExerciseCopy> entityModel = assembler.toModel(updatedExerciseCopy);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @DeleteMapping("/exercises/{id}")
    ResponseEntity<?> deleteExercise(@PathVariable Long id) {

        exerciseCopyRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
