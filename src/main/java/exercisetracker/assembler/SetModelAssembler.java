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

        return EntityModel.of(set,
                WebMvcLinkBuilder.linkTo(methodOn(SetController.class).one(set.getId())).withSelfRel(),
                linkTo(methodOn(SetController.class).all()).withRel("sets"));
    }
}
