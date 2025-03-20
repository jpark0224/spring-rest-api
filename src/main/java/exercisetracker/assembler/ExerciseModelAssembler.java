package exercisetracker.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.controller.ExerciseTemplateController;
import exercisetracker.model.ExerciseTemplate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExerciseModelAssembler implements RepresentationModelAssembler<ExerciseTemplate, EntityModel<ExerciseTemplate>> {

    @Override
    public EntityModel<ExerciseTemplate> toModel(ExerciseTemplate exerciseTemplate) {

        return EntityModel.of(exerciseTemplate,
                WebMvcLinkBuilder.linkTo(methodOn(ExerciseTemplateController.class).one(exerciseTemplate.getId())).withSelfRel(),
                linkTo(methodOn(ExerciseTemplateController.class).all()).withRel("exercises"));
    }
}
