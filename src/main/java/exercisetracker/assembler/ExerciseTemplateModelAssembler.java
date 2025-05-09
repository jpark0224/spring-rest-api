package exercisetracker.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.controller.ExerciseTemplateController;
import exercisetracker.model.ExerciseTemplate;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTemplateModelAssembler implements RepresentationModelAssembler<ExerciseTemplate, EntityModel<ExerciseTemplate>> {

    @Override
    public EntityModel<ExerciseTemplate> toModel(ExerciseTemplate exerciseTemplate) {

        return EntityModel.of(exerciseTemplate,
                WebMvcLinkBuilder.linkTo(methodOn(ExerciseTemplateController.class).getOneExerciseTemplate(exerciseTemplate.getId())).withSelfRel(),
                linkTo(methodOn(ExerciseTemplateController.class).getAllExerciseTemplates()).withRel("exercises"));
    }
}
