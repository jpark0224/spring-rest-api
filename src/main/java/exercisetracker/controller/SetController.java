package exercisetracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import exercisetracker.dto.SetDTO;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.SetNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.SetRepository;
import exercisetracker.assembler.SetModelAssembler;
import exercisetracker.model.Set;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs/{logId}/exercises/{exerciseCopyId}/sets")
public class SetController {

    private final SetRepository setRepository;
    private final SetModelAssembler assembler;
    private final ExerciseCopyRepository exerciseCopyRepository;

    public SetController(SetRepository setRepository, SetModelAssembler assembler, ExerciseCopyRepository exerciseCopyRepository) {

        this.setRepository = setRepository;
        this.assembler = assembler;
        this.exerciseCopyRepository = exerciseCopyRepository;
    }

    public Double calculate1RM(int rep, Double weight) {
        return weight / (1.0278 - ( 0.0278 * rep ));
    }

    public Set createSet(SetDTO setDto, Long exerciseCopyId) {
        ExerciseCopy exerciseCopy = exerciseCopyRepository.findById(exerciseCopyId)
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));

        int reps = setDto.getReps();
        Double weight = setDto.getWeight();
        Double oneRepMax = calculate1RM(reps, weight);

        return new Set(reps, weight, oneRepMax, exerciseCopy);
    }

    @GetMapping
    public CollectionModel<EntityModel<Set>> getAllSetsInExercise(@PathVariable Long logId,
                                                                  @PathVariable Long exerciseCopyId) {

        ExerciseCopy exerciseCopy = exerciseCopyRepository.findById(exerciseCopyId) //
                .orElseThrow(() -> new ExerciseCopyNotFoundException(exerciseCopyId));

        List<EntityModel<Set>> sets = setRepository.findByExerciseCopyOrderByIdAsc(exerciseCopy).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(sets,
                linkTo(methodOn(SetController.class).getAllSetsInExercise(logId, exerciseCopyId)).withSelfRel());
    }

    @GetMapping("/{setId}")
    public EntityModel<Set> getOneSet(@PathVariable Long logId,
                                      @PathVariable Long exerciseCopyId,
                                      @PathVariable Long setId) {

        Set set = setRepository.findById(setId) //
                .orElseThrow(() -> new SetNotFoundException(setId));

        return assembler.toModel(set);
    }

    @PostMapping
    ResponseEntity<EntityModel<Set>> postSet(@PathVariable Long logId,
                                            @PathVariable Long exerciseCopyId,
                                            @RequestBody @Valid SetDTO setDto) {

        Set newSet = createSet(setDto, exerciseCopyId);
        newSet.getExerciseCopy().addSet(newSet);
        Set savedSet = setRepository.save(newSet);

        return ResponseEntity
                .created(linkTo(methodOn(SetController.class).getOneSet(logId, exerciseCopyId, newSet.getId())).toUri())
                .body(assembler.toModel(savedSet));
    }

    @PutMapping("/{setId}")
    ResponseEntity<?> replaceSet(@PathVariable Long logId,
                                 @PathVariable Long exerciseCopyId,
                                 @PathVariable Long setId,
                                 @RequestBody @Valid SetDTO setDto) {

        Set updatedSet = setRepository.findById(setId)
                .map(existingSet -> {
                    existingSet.setReps(setDto.getReps());
                    existingSet.setWeight(setDto.getWeight());
                    existingSet.setOneRepMax(calculate1RM(setDto.getReps(), setDto.getWeight()));
                    return setRepository.save(existingSet);
                })
                .orElseGet(() -> {
                    Set newSet = createSet(setDto, exerciseCopyId);
                    newSet.getExerciseCopy().addSet(newSet);
                    return setRepository.save(newSet);
                });

        EntityModel<Set> entityModel = assembler.toModel(updatedSet);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{setId}")
    public ResponseEntity<Void> deleteSet(
            @PathVariable Long logId,
            @PathVariable Long exerciseCopyId,
            @PathVariable Long setId) {

        Set set = setRepository.findById(setId)
                .orElseThrow(() -> new SetNotFoundException(setId));

        setRepository.delete(set);

        return ResponseEntity.noContent().build();
    }
}
