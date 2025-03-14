package exercisetracker;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelAssembler implements RepresentationModelAssembler<Exercise, EntityModel<Exercise>> {

    @Override
    public EntityModel<Exercise> toModel(Exercise exercise) {

        return EntityModel.of(exercise,
                linkTo(methodOn(ExerciseController.class).one(exercise.getId())).withSelfRel(),
                linkTo(methodOn(ExerciseController.class).all()).withRel("exercises"));
    }
}
