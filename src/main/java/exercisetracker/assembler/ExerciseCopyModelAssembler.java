package exercisetracker.assembler;

import exercisetracker.controller.ExerciseCopyController;
import exercisetracker.model.ExerciseCopy;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExerciseCopyModelAssembler implements RepresentationModelAssembler<ExerciseCopy, EntityModel<ExerciseCopy>> {

    @Override
    public EntityModel<ExerciseCopy> toModel(ExerciseCopy exerciseCopy) {

        return EntityModel.of(exerciseCopy,
                WebMvcLinkBuilder.linkTo(methodOn(ExerciseCopyController.class).one(exerciseCopy.getId())).withSelfRel(),
                linkTo(methodOn(ExerciseCopyController.class).all()).withRel("exerciseCopies"));
    }
}
