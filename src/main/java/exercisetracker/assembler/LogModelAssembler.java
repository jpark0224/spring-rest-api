package exercisetracker.assembler;

import exercisetracker.controller.LogController;
import exercisetracker.controller.SetController;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.model.Log;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LogModelAssembler implements RepresentationModelAssembler<Log, EntityModel<Log>> {
    @Override
    public EntityModel<Log> toModel(Log log) {

        return EntityModel.of(log,
                WebMvcLinkBuilder.linkTo(methodOn(LogController.class).one(log.getId())).withSelfRel(),
                linkTo(methodOn(LogController.class).all()).withRel("logs"));
    }
}
