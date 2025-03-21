package exercisetracker.assembler;

import exercisetracker.controller.LogController;
import exercisetracker.controller.SetController;
import exercisetracker.model.Log;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LogModelAssembler {
    @Override
    public EntityModel<Log> toModel(Log log) {

        return EntityModel.of(log,
                WebMvcLinkBuilder.linkTo(methodOn(SetController.class).one(log.getId())).withSelfRel(),
                linkTo(methodOn(LogController.class).all()).withRel("logs"));
    }
}
