package exercisetracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.dto.SetDTO;
import exercisetracker.exception.ExerciseNotFoundException;
import exercisetracker.exception.SetNotFoundException;
import exercisetracker.model.Exercise;
import exercisetracker.repository.ExerciseRepository;
import exercisetracker.repository.SetRepository;
import exercisetracker.assembler.SetModelAssembler;
import exercisetracker.model.Set;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetController {

    private final SetRepository setRepository;
    private final SetModelAssembler assembler;
    private final ExerciseRepository exerciseRepository;

    SetController(SetRepository setRepository, SetModelAssembler assembler, ExerciseRepository exerciseRepository) {

        this.setRepository = setRepository;
        this.assembler = assembler;
        this.exerciseRepository = exerciseRepository;
    }

    public Set createSet(SetDTO setDto) {
        Exercise exercise = exerciseRepository.findById(setDto.getExerciseId())
                .orElseThrow(() -> new ExerciseNotFoundException(setDto.getExerciseId()));

        return new Set(setDto.getReps(), setDto.getWeight(), exercise);
    }

    @GetMapping("/sets")
    public CollectionModel<EntityModel<Set>> all() {

        List<EntityModel<Set>> sets = setRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sets,
                linkTo(methodOn(SetController.class).all()).withSelfRel());
    }

    @GetMapping("/exercises/{exerciseId}/sets")
    public CollectionModel<EntityModel<Set>> allInExercise(@PathVariable Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId) //
                    .orElseThrow(() -> new ExerciseNotFoundException(exerciseId));

        List<EntityModel<Set>> sets = setRepository.findByExerciseOrderByIdAsc(exercise).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sets,
                linkTo(methodOn(SetController.class).allInExercise(exerciseId)).withSelfRel());
    }

    @GetMapping("/sets/{id}")
    public EntityModel<Set> one(@PathVariable Long id) {

        Set order = setRepository.findById(id) //
                .orElseThrow(() -> new SetNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/sets")
    ResponseEntity<EntityModel<Set>> newSet(@RequestBody SetDTO setDto) {

        Set newSet = setRepository.save(createSet(setDto));

        return ResponseEntity
                .created(linkTo(methodOn(SetController.class).one(newSet.getId())).toUri())
                .body(assembler.toModel(newSet));
    }

    @PutMapping("/sets/{id}")
    ResponseEntity<?> replaceSet(@RequestBody SetDTO setDto, @PathVariable Long id) {

        Set newSet = createSet(setDto);

        Set updatedSet = setRepository.findById(id)
                .map(set -> {
                    set.setReps(newSet.getReps());
                    set.setWeight(newSet.getWeight());
                    set.setExercise(newSet.getExercise());
                    return setRepository.save(set);
                })
                .orElseGet(() -> {
                    return setRepository.save(newSet);
                });

        EntityModel<Set> entityModel = assembler.toModel(updatedSet);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/sets/{id}")
    ResponseEntity<?> deleteSet(@PathVariable Long id) {

        setRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
