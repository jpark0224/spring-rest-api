package exercisetracker;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.apache.coyote.Response;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseModelAssembler assembler;

    ExerciseController(ExerciseRepository exerciseRepository, ExerciseModelAssembler assembler) {

        this.exerciseRepository = exerciseRepository;
        this.assembler = assembler;
    }

    @GetMapping("/exercises")
    CollectionModel<EntityModel<Exercise>> all() {

        List<EntityModel<Exercise>> exercises = exerciseRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(exercises,
                linkTo(methodOn(ExerciseController.class).all()).withSelfRel());
    }

    @GetMapping("/exercises/{id}")
    EntityModel<Exercise> one(@PathVariable Long id) {

        Exercise order = exerciseRepository.findById(id) //
                .orElseThrow(() -> new ExerciseNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/exercises")
    ResponseEntity<EntityModel<Exercise>> newExercise(@RequestBody Exercise exercise) {

        Exercise newExercise = exerciseRepository.save(exercise);

        return ResponseEntity //
                .created(linkTo(methodOn(ExerciseController.class).one(newExercise.getId())).toUri()) //
                .body(assembler.toModel(newExercise));
    }

    @PutMapping("/exercises/{id}")
    ResponseEntity<?> replaceExercise(@RequestBody Exercise newExercise, @PathVariable Long id) {

        Exercise updatedExercise = exerciseRepository.findById(id)
                .map(exercise -> {
                    exercise.setName(newExercise.getName());
                    exercise.setPrimaryMuscleGroup(newExercise.getPrimaryMuscleGroup());
                    return exerciseRepository.save(exercise);
                })
                .orElseGet(() -> {
                    return exerciseRepository.save(newExercise);
                });

        EntityModel<Exercise> entityModel = assembler.toModel(updatedExercise);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    @DeleteMapping("/exercises/{id}")
    ResponseEntity<?> deleteExercise(@PathVariable Long id) {

        exerciseRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
