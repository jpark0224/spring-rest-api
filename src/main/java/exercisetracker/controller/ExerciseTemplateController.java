package exercisetracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.assembler.ExerciseTemplateModelAssembler;
import exercisetracker.model.ExerciseTemplate;
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
    private final ExerciseTemplateModelAssembler assembler;

    public ExerciseTemplateController(ExerciseTemplateRepository exerciseTemplateRepository, ExerciseTemplateModelAssembler assembler) {

        this.exerciseTemplateRepository = exerciseTemplateRepository;
        this.assembler = assembler;
    }

    @GetMapping("/exercises")
    public CollectionModel<EntityModel<ExerciseTemplate>> all() {

        List<EntityModel<ExerciseTemplate>> exercises = exerciseTemplateRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseTemplateController.class).all()).withSelfRel());
    }

    @GetMapping("/exercises/{id}")
    public EntityModel<ExerciseTemplate> one(@PathVariable Long id) {

        ExerciseTemplate exerciseTemplate = exerciseTemplateRepository.findById(id) //
                .orElseThrow(() -> new ExerciseTemplateNotFoundException(id));

        return assembler.toModel(exerciseTemplate);
    }

    @PostMapping("/exercises")
    ResponseEntity<EntityModel<ExerciseTemplate>> newExercise(@RequestBody ExerciseTemplate exerciseTemplate) {

        ExerciseTemplate newExerciseTemplate = exerciseTemplateRepository.save(exerciseTemplate);

        return ResponseEntity //
                .created(linkTo(methodOn(ExerciseTemplateController.class).one(newExerciseTemplate.getId())).toUri()) //
                .body(assembler.toModel(newExerciseTemplate));
    }

    @PutMapping("/exercises/{id}")
    ResponseEntity<?> replaceExercise(@RequestBody ExerciseTemplate newExerciseTemplate, @PathVariable Long id) {

        ExerciseTemplate updatedExerciseTemplate = exerciseTemplateRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(newExerciseTemplate.getName());
                    exercise.setPrimaryMuscleGroup(newExerciseTemplate.getPrimaryMuscleGroup());
                    return exerciseTemplateRepository.save(exercise);
                })
                .orElseGet(() -> {
                    return exerciseTemplateRepository.save(newExerciseTemplate);
                });

        EntityModel<ExerciseTemplate> entityModel = assembler.toModel(updatedExerciseTemplate);

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
