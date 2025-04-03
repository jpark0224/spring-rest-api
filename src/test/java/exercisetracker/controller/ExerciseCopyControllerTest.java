package exercisetracker.controller;

import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseCopyControllerTest {

    @Mock
    private ExerciseCopyModelAssembler assembler;

    @Mock
    private ExerciseCopyRepository exerciseCopyRepository;

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private ExerciseCopyController exerciseCopyController;

    private Log savedLog;
    private Long validLogId;
    private Long invalidLogId;
    private ExerciseCopy savedBarbellSquatCopy;
    private ExerciseCopy savedRomanianDeadliftCopy;
    private String savedBarbellSquatCopyLink;
    private String savedRomanianDeadliftCopyLink;

    @BeforeEach
    void setUp() {
        savedLog = new Log();
        validLogId = 1L;
        invalidLogId = 99L;
        savedLog.setId(validLogId);

        savedBarbellSquatCopy = new ExerciseCopy();
        savedBarbellSquatCopy.setId(1L);
        savedBarbellSquatCopy.setName("barbell squat");
        savedBarbellSquatCopy.setPrimaryMuscleGroup("hamstrings");
        savedBarbellSquatCopy.setLog(savedLog);
        savedBarbellSquatCopyLink = "/logs/" + validLogId + "/exercises/" + savedBarbellSquatCopy.getId();

        savedRomanianDeadliftCopy = new ExerciseCopy();
        savedRomanianDeadliftCopy.setId(2L);
        savedRomanianDeadliftCopy.setName("romanian deadlift");
        savedRomanianDeadliftCopy.setPrimaryMuscleGroup("quads");
        savedRomanianDeadliftCopy.setLog(savedLog);
        savedRomanianDeadliftCopyLink = "/logs/" + validLogId + "/exercises/" + savedRomanianDeadliftCopy.getId();
    }

    @Nested
    class GetAllTest {

        @Test
        void throwsException_givenInvalidLogId() {
            when(logRepository.existsById(invalidLogId)).thenReturn(false);

            assertThatThrownBy(() -> exerciseCopyController.getAllExerciseCopiesInLog(invalidLogId))
                    .isInstanceOf(LogNotFoundException.class);
        }

        @Test
        void returnsCollectionModel_givenValidLogId() {
            List<ExerciseCopy> exerciseCopies = List.of(savedBarbellSquatCopy, savedRomanianDeadliftCopy);

            EntityModel<ExerciseCopy> barbellSquatEntityModel = EntityModel.of(savedBarbellSquatCopy);
            barbellSquatEntityModel.add(Link.of(savedBarbellSquatCopyLink).withSelfRel());

            EntityModel<ExerciseCopy> romanianDeadliftEntityModel = EntityModel.of(savedRomanianDeadliftCopy);
            romanianDeadliftEntityModel.add(Link.of(savedRomanianDeadliftCopyLink).withSelfRel());

            when(logRepository.existsById(validLogId)).thenReturn(true);
            when(exerciseCopyRepository.findByLogId(validLogId)).thenReturn(exerciseCopies);
            when(assembler.toModel(savedBarbellSquatCopy)).thenReturn(barbellSquatEntityModel);
            when(assembler.toModel(savedRomanianDeadliftCopy)).thenReturn(romanianDeadliftEntityModel);

            CollectionModel<EntityModel<ExerciseCopy>> result = exerciseCopyController.getAllExerciseCopiesInLog(validLogId);

            assertThat(result.getContent()).containsExactlyInAnyOrder(barbellSquatEntityModel, romanianDeadliftEntityModel);
            assertThat(result.getLinks()).anyMatch(link -> link.getRel().value().equals("self"));
            result.getContent().forEach(model ->
                            assertThat(model.getLinks()).anyMatch(link -> link.getRel().value().equals("self"))
            );
        }
    }

    @Nested
    class GetOneTest {

        @Test
        void returnsEntityModel_givenValidId() {
            EntityModel<ExerciseCopy> barbellSquatEntityModel = EntityModel.of(savedBarbellSquatCopy);
            barbellSquatEntityModel.add(Link.of(savedBarbellSquatCopyLink).withSelfRel());

            when(exerciseCopyRepository.findById(savedBarbellSquatCopy.getId())).thenReturn(Optional.of(savedBarbellSquatCopy));
            when(assembler.toModel(savedBarbellSquatCopy)).thenReturn(barbellSquatEntityModel);

            EntityModel<ExerciseCopy> result = exerciseCopyController.getOneExerciseCopyInLog(validLogId, savedBarbellSquatCopy.getId());

            assertThat(result.getContent()).isEqualTo(savedBarbellSquatCopy);
            assertThat(result.getLinks()).anyMatch(link ->
                    link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatCopyLink));
        }

        @Test
        void throwsException_givenInvalidExerciseCopyId() {
            Long invalidExerciseCopyId = 999L;
            when(exerciseCopyRepository.findById(invalidExerciseCopyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exerciseCopyController.getOneExerciseCopyInLog(validLogId, invalidExerciseCopyId))
                    .isInstanceOf(ExerciseCopyNotFoundException.class);
        }

        @Test
        void throwsException_givenInvalidLogId() {
            when(exerciseCopyRepository.findById(savedBarbellSquatCopy.getId())).thenReturn(Optional.of(savedBarbellSquatCopy));

            assertThatThrownBy(() -> exerciseCopyController.getOneExerciseCopyInLog(invalidLogId, savedBarbellSquatCopy.getId()))
                    .isInstanceOf(ExerciseCopyNotFoundException.class);
        }
    }
}
