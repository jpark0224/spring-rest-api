package exercisetracker.controller;

import exercisetracker.assembler.PersonalRecordModelAssembler;
import exercisetracker.exception.PersonalRecordNotFoundException;
import exercisetracker.model.PersonalRecord;
import exercisetracker.repository.PersonalRecordRepository;
import exercisetracker.service.PersonalRecordService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PersonalRecordController {
    private final PersonalRecordService personalRecordService;
    private final PersonalRecordRepository personalRecordRepository;
    private final PersonalRecordModelAssembler assembler;
    public PersonalRecordController(PersonalRecordService personalRecordService, PersonalRecordRepository personalRecordRepository, PersonalRecordModelAssembler assembler) {

        this.personalRecordService = personalRecordService;
        this.personalRecordRepository = personalRecordRepository;
        this.assembler = assembler;
    }

    @GetMapping("/personalRecords")
    public CollectionModel<EntityModel<PersonalRecord>> getAllPersonalRecords() {

        List<EntityModel<PersonalRecord>> personalRecords = personalRecordRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                personalRecords,
                linkTo(methodOn(PersonalRecordController.class).getAllPersonalRecords()).withSelfRel()
        );
    }

    @GetMapping("/personalRecords/{id}")
    public EntityModel<PersonalRecord> getOnePersonalRecord(@PathVariable Long id) {

        PersonalRecord personalRecords = personalRecordRepository.findById(id)
                .orElseThrow(() -> new PersonalRecordNotFoundException(id));

        return assembler.toModel(personalRecords);
    }

//    @PostMapping("/personalRecords")
//    ResponseEntity<EntityModel<PersonalRecord>> postPersonalRecord(@RequestBody PersonalRecord personalRecord) {
//
//        PersonalRecord newPersonalRecord = personalRecordService.createPersonalRecord(personalRecord);
//
//        return ResponseEntity
//                .created(linkTo(methodOn(PersonalRecordController.class).getOnePersonalRecord(newPersonalRecord.getId())).toUri())
//                .body(assembler.toModel(newPersonalRecord));
//    }

    @DeleteMapping("/personalRecords/{id}")
    ResponseEntity<EntityModel<PersonalRecord>> deletePersonalRecord(@PathVariable Long id) {
        if (!personalRecordRepository.existsById(id)) {
            throw new PersonalRecordNotFoundException(id);
        }

        personalRecordRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
