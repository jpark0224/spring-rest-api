package exercisetracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.assembler.ExerciseModelAssembler;
import exercisetracker.model.Exercise;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseTemplateController {

    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final ExerciseModelAssembler assembler;

    ExerciseTemplateController(ExerciseTemplateRepository exerciseTemplateRepository, ExerciseModelAssembler assembler) {

        this.exerciseTemplateRepository = exerciseTemplateRepository;
        this.assembler = assembler;
    }

    @GetMapping("/exercises")
    public CollectionModel<EntityModel<Exercise>> all() {

        List<EntityModel<Exercise>> exercises = exerciseTemplateRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseTemplateController.class).all()).withSelfRel());
    }

    @GetMapping("/exercises/{id}")
    public EntityModel<Exercise> one(@PathVariable Long id) {

        Exercise exercise = exerciseTemplateRepository.findById(id) //
                .orElseThrow(() -> new ExerciseTemplateNotFoundException(id));

        return assembler.toModel(exercise);
    }

    @PostMapping("/exercises")
    ResponseEntity<EntityModel<Exercise>> newExercise(@RequestBody Exercise exercise) {

        Exercise newExercise = exerciseTemplateRepository.save(exercise);

        return ResponseEntity //
                .created(linkTo(methodOn(ExerciseTemplateController.class).one(newExercise.getId())).toUri()) //
                .body(assembler.toModel(newExercise));
    }

    @PutMapping("/exercises/{id}")
    ResponseEntity<?> replaceExercise(@RequestBody Exercise newExercise, @PathVariable Long id) {

        Exercise updatedExercise = exerciseTemplateRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(newExercise.getName());
                    exercise.setPrimaryMuscleGroup(newExercise.getPrimaryMuscleGroup());
                    return exerciseTemplateRepository.save(exercise);
                })
                .orElseGet(() -> {
                    return exerciseTemplateRepository.save(newExercise);
                });

        EntityModel<Exercise> entityModel = assembler.toModel(updatedExercise);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @DeleteMapping("/exercises/{id}")
    ResponseEntity<?> deleteExercise(@PathVariable Long id) {

        exerciseTemplateRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
