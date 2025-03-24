package exercisetracker.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.controller.SetController;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import exercisetracker.model.Set;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

@Component
public class SetModelAssembler implements RepresentationModelAssembler<Set, EntityModel<Set>> {

    @Override
    public EntityModel<Set> toModel(Set set) {

        return EntityModel.of(set,
                WebMvcLinkBuilder.linkTo(methodOn(SetController.class).one(set.getExerciseCopy().getLog().getId(),
                        set.getExerciseCopy().getId(),
                        set.getId())).withSelfRel(),
                linkTo(methodOn(SetController.class)
                        .allInExercise(set.getExerciseCopy().getLog().getId(),
                                set.getExerciseCopy().getId()))
                        .withRel("sets"));
    }
}
