package exercisetracker.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.controller.SetController;
import exercisetracker.model.Set;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class SetModelAssembler implements RepresentationModelAssembler<Set, EntityModel<Set>> {

    @Override
    public EntityModel<Set> toModel(Set set) {

        Long logId = set.getExerciseCopy().getLog().getId();
        Long exerciseCopyId = set.getExerciseCopy().getId();
        Long setId = set.getId();

        EntityModel<Set> model = EntityModel.of(set,
                WebMvcLinkBuilder.linkTo(methodOn(SetController.class).getOneSet(logId,
                        exerciseCopyId,
                        setId)).withSelfRel(),
                linkTo(methodOn(SetController.class)
                        .getAllSetsInExercise(logId, exerciseCopyId))
                        .withRel("sets"));

        set.setExerciseCopy(null);

        return model;
    }
}
