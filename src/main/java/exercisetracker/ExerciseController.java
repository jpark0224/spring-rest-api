package exercisetracker;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
}
