package exercisetracker.controller;

import exercisetracker.assembler.ExerciseTemplateModelAssembler;
import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.repository.ExerciseTemplateRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ExerciseTemplateControllerTest {

    @Mock
    private ExerciseTemplateModelAssembler assembler;

    @Mock
    private ExerciseTemplateRepository exerciseTemplateRepository;

    @InjectMocks
    private ExerciseTemplateController exerciseTemplateController;
    
    private ExerciseTemplate requestBarbellSquat;
    private ExerciseTemplate requestRomanianDeadlift;
    private ExerciseTemplate savedBarbellSquat;
    private ExerciseTemplate savedRomanianDeadlift;
    private String savedBarbellSquatLink;
    private String savedRomanianDeadliftLink;

    @BeforeEach
    void setUp() {
        requestBarbellSquat = new ExerciseTemplate();
        requestBarbellSquat.setName("barbell squat");
        requestBarbellSquat.setPrimaryMuscleGroup("hamstrings");

        savedBarbellSquat = new ExerciseTemplate();
        savedBarbellSquat.setName(requestBarbellSquat.getName());
        savedBarbellSquat.setPrimaryMuscleGroup(requestBarbellSquat.getPrimaryMuscleGroup());
        savedBarbellSquat.setId(1L);
        savedBarbellSquatLink = "/exercises/" + savedBarbellSquat.getId();

        requestRomanianDeadlift = new ExerciseTemplate();
        requestRomanianDeadlift.setName("romanian deadlift");
        requestRomanianDeadlift.setPrimaryMuscleGroup("quads");

        savedRomanianDeadlift = new ExerciseTemplate();
        savedRomanianDeadlift.setName(requestRomanianDeadlift.getName());
        savedRomanianDeadlift.setPrimaryMuscleGroup(requestRomanianDeadlift.getPrimaryMuscleGroup());
        savedRomanianDeadlift.setId(2L);
        savedRomanianDeadliftLink = "/exercises/" + savedRomanianDeadlift.getId();
    }


    @Nested
    class GetAllTest {

        @Test
        void returnsCollectionModel() {
            List<ExerciseTemplate> exerciseTemplates = List.of(savedBarbellSquat, savedRomanianDeadlift);

            EntityModel<ExerciseTemplate> barbellSquatEntityModel = EntityModel.of(savedBarbellSquat);
            barbellSquatEntityModel.add(Link.of(savedBarbellSquatLink).withSelfRel());

            EntityModel<ExerciseTemplate> romanianDeadliftEntityModel = EntityModel.of(savedRomanianDeadlift);
            romanianDeadliftEntityModel.add(Link.of(savedRomanianDeadliftLink).withSelfRel());

            when(exerciseTemplateRepository.findAll()).thenReturn(exerciseTemplates);
            when(assembler.toModel(savedBarbellSquat)).thenReturn(barbellSquatEntityModel);
            when(assembler.toModel(savedRomanianDeadlift)).thenReturn(romanianDeadliftEntityModel);

            CollectionModel<EntityModel<ExerciseTemplate>> result = exerciseTemplateController.getAllExerciseTemplates();

            assertThat(result.getContent()).containsExactlyInAnyOrder(barbellSquatEntityModel, romanianDeadliftEntityModel);
            assertThat(result.getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self"));
            result.getContent().forEach(model ->
                    assertThat(model.getLinks())
                            .anyMatch(link -> link.getRel().value().equals("self"))
            );
        }
    }

    @Nested
    class GetOneTests {

        @Test
        void returnsEntityModel_givenValidId() {
            EntityModel<ExerciseTemplate> barbellSquatEntityModel = EntityModel.of(savedBarbellSquat);
            barbellSquatEntityModel.add(Link.of(savedBarbellSquatLink).withSelfRel());

            when(exerciseTemplateRepository.findById(savedBarbellSquat.getId())).thenReturn(Optional.of(savedBarbellSquat));
            when(assembler.toModel(savedBarbellSquat)).thenReturn(barbellSquatEntityModel);

            EntityModel<ExerciseTemplate> result = exerciseTemplateController.getOneExerciseTemplate(savedBarbellSquat.getId());

            assertThat(result.getContent()).isEqualTo(savedBarbellSquat);
            assertThat(result.getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatLink));
        }

        @Test
        void throwsException_givenInvalidId() {
            Long invalidExerciseTemplateId = 999L;

            when(exerciseTemplateRepository.findById(invalidExerciseTemplateId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exerciseTemplateController.getOneExerciseTemplate(invalidExerciseTemplateId)).isInstanceOf(ExerciseTemplateNotFoundException.class);
        }
    }

    @Nested
    class PostTest {

        @Test
        void returnsCreatedResponseWithLocationHeaderAndBody() {
            EntityModel<ExerciseTemplate> model = EntityModel.of(savedBarbellSquat);
            model.add(Link.of(savedBarbellSquatLink).withSelfRel());

            when(exerciseTemplateRepository.save(requestBarbellSquat)).thenReturn(savedBarbellSquat);
            when(assembler.toModel(savedBarbellSquat)).thenReturn(model);

            ResponseEntity<EntityModel<ExerciseTemplate>> response =
                    exerciseTemplateController.postExerciseTemplate(requestBarbellSquat);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo(savedBarbellSquatLink);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isEqualTo(savedBarbellSquat);
            assertThat(response.getBody().getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatLink));
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void returnsCreatedResponseWithLocationHeaderAndBody_givenNonExistingId() {
            EntityModel<ExerciseTemplate> model = EntityModel.of(savedBarbellSquat);
            model.add(Link.of(savedBarbellSquatLink).withSelfRel());

            Long nonExistingId = 99L;

            when(exerciseTemplateRepository.findById(nonExistingId)).thenReturn(Optional.empty());
            when(exerciseTemplateRepository.save(requestBarbellSquat)).thenReturn(savedBarbellSquat);
            when(assembler.toModel(savedBarbellSquat)).thenReturn(model);

            ResponseEntity<EntityModel<ExerciseTemplate>> response =
                    exerciseTemplateController.replaceExerciseTemplate(requestBarbellSquat, nonExistingId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo(savedBarbellSquatLink);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getContent()).isEqualTo(savedBarbellSquat);
            assertThat(response.getBody().getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatLink));
        }

        @Test
        void returnsCreatedResponseWithLocationHeaderAndBody_givenExistingId() {
            EntityModel<ExerciseTemplate> barbellSquatEntityModel = EntityModel.of(requestBarbellSquat);
            barbellSquatEntityModel.add(Link.of(savedBarbellSquatLink).withSelfRel());

            when(exerciseTemplateRepository.findById(requestBarbellSquat.getId())).thenReturn(Optional.of(requestBarbellSquat));
            when(exerciseTemplateRepository.save(any(ExerciseTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(assembler.toModel(requestBarbellSquat)).thenReturn(barbellSquatEntityModel);

            ResponseEntity<EntityModel<ExerciseTemplate>> response = exerciseTemplateController.replaceExerciseTemplate(requestRomanianDeadlift, requestBarbellSquat.getId());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo(savedBarbellSquatLink);
            assertThat(Objects.requireNonNull(response.getBody()).getContent()).isEqualTo(requestBarbellSquat);
            assertThat(requestBarbellSquat.getName()).isEqualTo("romanian deadlift");
            assertThat(requestBarbellSquat.getPrimaryMuscleGroup()).isEqualTo("quads");
            assertThat(response.getBody().getLinks())
                    .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals(savedBarbellSquatLink));
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void throwsException_givenInvalidId() {
            Long Id = 99L;

            when(exerciseTemplateRepository.existsById(Id)).thenReturn(false);

            assertThatThrownBy(() -> exerciseTemplateController.deleteExerciseTemplate(Id))
                    .isInstanceOf(ExerciseTemplateNotFoundException.class)
                    .hasMessageContaining("Could not find exercise template");
        }

        @Test
        void deletesLogAndReturnsNoContent_givenValidId() {
            Long Id = 1L;

            when(exerciseTemplateRepository.existsById(Id)).thenReturn(true);

            ResponseEntity<EntityModel<ExerciseTemplate>> response = exerciseTemplateController.deleteExerciseTemplate(Id);

            verify(exerciseTemplateRepository).deleteById(Id);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

}
