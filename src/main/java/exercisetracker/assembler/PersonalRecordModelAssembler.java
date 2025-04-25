package exercisetracker.assembler;

import exercisetracker.controller.LogController;
import exercisetracker.controller.PersonalRecordController;
import exercisetracker.model.Log;
import exercisetracker.model.PersonalRecord;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonalRecordModelAssembler implements RepresentationModelAssembler<PersonalRecord, EntityModel<PersonalRecord>> {
    @Override
    public EntityModel<PersonalRecord> toModel(PersonalRecord personalRecord) {

        return EntityModel.of(personalRecord,
                WebMvcLinkBuilder.linkTo(methodOn(PersonalRecordController.class).getOnePersonalRecord(personalRecord.getId())).withSelfRel(),
                linkTo(methodOn(PersonalRecordController.class).getAllPersonalRecords()).withRel("personalRecords"));
    }
}
