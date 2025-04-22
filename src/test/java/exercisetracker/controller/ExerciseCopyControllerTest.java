package exercisetracker.controller;

import exercisetracker.assembler.ExerciseCopyModelAssembler;
import exercisetracker.exception.ExerciseCopyNotFoundException;
import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseCopy;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseCopyRepository;
import exercisetracker.repository.ExerciseTemplateRepository;
import exercisetracker.repository.LogRepository;
import exercisetracker.service.LogService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ExerciseCopyControllerTest {

    @Mock
    private ExerciseCopyModelAssembler assembler;

    @Mock
    private ExerciseCopyRepository exerciseCopyRepository;

    @Mock
    private ExerciseTemplateRepository exerciseTemplateRepository;

    @Mock
    private LogRepository logRepository;

    @Mock
    private LogService logService;

    @InjectMocks
    private ExerciseCopyController exerciseCopyController;

    private Log savedLog;
    private Long validLogId;
    private Long invalidLogId;
    private ExerciseCopy savedBarbellSquatCopy;
    private String savedBarbellSquatCopyLink;

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
    }

    @Nested
    class GetAllTest {

        private ExerciseCopy savedRomanianDeadliftCopy;
        private String savedRomanianDeadliftCopyLink;

        @BeforeEach
        void init() {
            savedRomanianDeadliftCopy = new ExerciseCopy();
            savedRomanianDeadliftCopy.setId(2L);
            savedRomanianDeadliftCopy.setName("romanian deadlift");
            savedRomanianDeadliftCopy.setPrimaryMuscleGroup("quads");
            savedRomanianDeadliftCopy.setLog(savedLog);
            savedRomanianDeadliftCopyLink = "/logs/" + validLogId + "/exercises/" + savedRomanianDeadliftCopy.getId();
        }

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

    @Nested
    class PostTests {

        private final Long validTemplateId = 1L;
        private final Long invalidTemplateId = 99L;

        @Test
        void throwsException_givenInvalidLogId() {
            when(logRepository.existsById(invalidLogId)).thenReturn(false);

            assertThatThrownBy(() -> exerciseCopyController.addExerciseCopyToLog(invalidLogId, validTemplateId))
                    .isInstanceOf(LogNotFoundException.class);
        }

        @Test
        void throwsException_givenInvalidTemplateId() {
            when(logRepository.existsById(validLogId)).thenReturn(true);
            when(exerciseTemplateRepository.existsById(invalidTemplateId)).thenReturn(false);

            assertThatThrownBy(() -> exerciseCopyController.addExerciseCopyToLog(validLogId, invalidTemplateId))
                    .isInstanceOf(ExerciseTemplateNotFoundException.class);
        }

        @Test
        void returnsCreatedResponseWithLocationHeaderAndBody() {
            EntityModel<ExerciseCopy> barbellSquatModel = EntityModel.of(savedBarbellSquatCopy);
            barbellSquatModel.add(Link.of(savedBarbellSquatCopyLink).withSelfRel());

            when(logRepository.existsById(validLogId)).thenReturn(true);
            when(exerciseTemplateRepository.existsById(validTemplateId)).thenReturn(true);
            when(logService.addExerciseToLog(validLogId, validTemplateId)).thenReturn(savedBarbellSquatCopy);
            when(assembler.toModel(savedBarbellSquatCopy)).thenReturn(barbellSquatModel);

            ResponseEntity<EntityModel<ExerciseCopy>> response = exerciseCopyController.addExerciseCopyToLog(validLogId, validTemplateId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo(savedBarbellSquatCopyLink);
            assertThat(Objects.requireNonNull(response.getBody()).getContent()).isEqualTo(savedBarbellSquatCopy);
            assertThat(response.getBody().getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatCopyLink));
        }
    }

    @Nested
    class DeleteTests {

        private final Long validExerciseCopyId = 1L;
        private final Long invalidExerciseCopyId = 99L;

        @Test
        void throwsException_givenInvalidLogId() {
            when(logRepository.existsById(invalidLogId)).thenReturn(false);

            assertThatThrownBy(() -> exerciseCopyController.deleteExerciseCopy(invalidLogId, validExerciseCopyId))
                    .isInstanceOf(LogNotFoundException.class);
        }

        @Test
        void throwsException_givenInvalidExerciseCopyId() {
            when(logRepository.existsById(validLogId)).thenReturn(true);
            doThrow(new ExerciseCopyNotFoundException(invalidExerciseCopyId))
                    .when(logService).removeExerciseFromLog(validLogId, invalidExerciseCopyId);

            assertThatThrownBy(() -> exerciseCopyController.deleteExerciseCopy(validLogId, invalidExerciseCopyId))
                    .isInstanceOf(ExerciseCopyNotFoundException.class);
        }

        @Test
        void deletesExerciseCopyAndReturnsNoContent_givenIds() {
            when(logRepository.existsById(validLogId)).thenReturn(true);

            ResponseEntity<EntityModel<ExerciseCopy>> response = exerciseCopyController.deleteExerciseCopy(validLogId, validExerciseCopyId);

            verify(logService).removeExerciseFromLog(validLogId, validExerciseCopyId);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
