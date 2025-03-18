package exercisetracker.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.controller.ExerciseController;
import exercisetracker.model.Exercise;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelAssembler implements RepresentationModelAssembler<Exercise, EntityModel<Exercise>> {

    @Override
    public EntityModel<Exercise> toModel(Exercise exercise) {

        return EntityModel.of(exercise,
                WebMvcLinkBuilder.linkTo(methodOn(ExerciseController.class).one(exercise.getId())).withSelfRel(),
                linkTo(methodOn(ExerciseController.class).all()).withRel("exercises"));
    }
}
