package exercisetracker.controller;

import exercisetracker.assembler.ExerciseTemplateModelAssembler;
import exercisetracker.exception.ExerciseTemplateNotFoundException;
import exercisetracker.exception.GlobalExceptionHandler;
import exercisetracker.exception.LogNotFoundException;
import exercisetracker.model.ExerciseTemplate;
import exercisetracker.model.Log;
import exercisetracker.repository.ExerciseTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseTemplateControllerTest {

    @Mock
    private ExerciseTemplateModelAssembler assembler;

    @Mock
    private ExerciseTemplateRepository exerciseTemplateRepository;

    @InjectMocks
    private ExerciseTemplateController exerciseTemplateController;

    @Test
    void testGetAllExerciseTemplates_returnsCollectionModel() {
        ExerciseTemplate exerciseTemplate1 = new ExerciseTemplate();
        exerciseTemplate1.setId(1L);
        exerciseTemplate1.setName("barbell squat");
        exerciseTemplate1.setPrimaryMuscleGroup("hamstrings");

        ExerciseTemplate exerciseTemplate2 = new ExerciseTemplate();
        exerciseTemplate2.setId(2L);
        exerciseTemplate2.setName("romanian deadlift");
        exerciseTemplate2.setPrimaryMuscleGroup("quads");

        List<ExerciseTemplate> exerciseTemplates = List.of(exerciseTemplate1, exerciseTemplate2);

        EntityModel<ExerciseTemplate> exerciseTemplateEntityModel1 = EntityModel.of(exerciseTemplate1);
        exerciseTemplateEntityModel1.add(Link.of("/exercises/1").withSelfRel());

        EntityModel<ExerciseTemplate> exerciseTemplateEntityModel2 = EntityModel.of(exerciseTemplate2);
        exerciseTemplateEntityModel2.add(Link.of("/exercises/2").withSelfRel());

        when(exerciseTemplateRepository.findAll()).thenReturn(exerciseTemplates);
        when(assembler.toModel(exerciseTemplate1)).thenReturn(exerciseTemplateEntityModel1);
        when(assembler.toModel(exerciseTemplate2)).thenReturn(exerciseTemplateEntityModel2);

        CollectionModel<EntityModel<ExerciseTemplate>> result = exerciseTemplateController.getAllExerciseTemplates();

        assertThat(result.getContent()).containsExactlyInAnyOrder(exerciseTemplateEntityModel1, exerciseTemplateEntityModel2);
        assertThat(result.getLinks())
                .anyMatch(link -> link.getRel().value().equals("self"));
        result.getContent().forEach(model ->
                assertThat(model.getLinks())
                        .anyMatch(link -> link.getRel().value().equals("self"))
        );
    }

    @Test
    void testGetOneExerciseTemplate_returnsEntityModel_givenValidId() {
        ExerciseTemplate exerciseTemplate = new ExerciseTemplate();
        Long exerciseTemplateId = 1L;
        exerciseTemplate.setId(exerciseTemplateId);
        exerciseTemplate.setName("barbell squat");
        exerciseTemplate.setPrimaryMuscleGroup("hamstrings");

        EntityModel<ExerciseTemplate> exerciseTemplateEntityModel = EntityModel.of(exerciseTemplate);
        exerciseTemplateEntityModel.add(Link.of("/exercises/1").withSelfRel());

        when(exerciseTemplateRepository.findById(exerciseTemplateId)).thenReturn(Optional.of(exerciseTemplate));
        when(assembler.toModel(exerciseTemplate)).thenReturn(exerciseTemplateEntityModel);

        EntityModel<ExerciseTemplate> result = exerciseTemplateController.getOneExerciseTemplate(exerciseTemplateId);

        assertThat(result.getContent()).isEqualTo(exerciseTemplate);
        assertThat(result.getLinks())
                .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals("/exercises/1"));
    }

    @Test
    void testGetOneExerciseTemplate_throwsException_givenInvalidId() {

        Long validExerciseTemplateId = 1L;
        Long invalidExerciseTemplateId = 999L;

        ExerciseTemplate exerciseTemplate = new ExerciseTemplate();
        exerciseTemplate.setId(validExerciseTemplateId);
        exerciseTemplate.setName("barbell squat");
        exerciseTemplate.setPrimaryMuscleGroup("hamstrings");

        when(exerciseTemplateRepository.findById(invalidExerciseTemplateId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> exerciseTemplateController.getOneExerciseTemplate(invalidExerciseTemplateId)).isInstanceOf(ExerciseTemplateNotFoundException.class);
    }

    @Test
    void testPostExerciseTemplate_returnsCreatedResponseWithLocationHeaderAndBody() {
        ExerciseTemplate exerciseTemplate = new ExerciseTemplate();
        exerciseTemplate.setId(1L);
        exerciseTemplate.setName("barbell squat");
        exerciseTemplate.setPrimaryMuscleGroup("hamstrings");

        EntityModel<ExerciseTemplate> exerciseTemplateModel = EntityModel.of(exerciseTemplate);
        exerciseTemplateModel.add(Link.of("/exercises/1").withSelfRel());

        when(exerciseTemplateRepository.save(any(ExerciseTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(assembler.toModel(exerciseTemplate)).thenReturn(exerciseTemplateModel);

        ResponseEntity<EntityModel<ExerciseTemplate>> response = exerciseTemplateController.postExerciseTemplate(exerciseTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getHeaders().getLocation()).toString()).isEqualTo("/exercises/1");
        assertThat(Objects.requireNonNull(response.getBody()).getContent()).isEqualTo(exerciseTemplate);
        assertThat(response.getBody().getLinks())
                .anyMatch(link -> link.getRel().value().equals("self") && link.getHref().equals("/exercises/1"));
    }

    @Test
    void testPostExerciseTemplate_returnsBadRequest_whenNameIsMissing() {
        ExerciseTemplate invalidTemplate = new ExerciseTemplate();
        invalidTemplate.setPrimaryMuscleGroup("hamstrings");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidTemplate, "exerciseTemplate");
        bindingResult.rejectValue("name", "NotBlank", "Name is required");

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        GlobalExceptionHandler.ErrorResponse errorResponse = handler.handleValidationExceptions(exception);

        assertThat(errorResponse.status()).isEqualTo(400);
        assertThat(errorResponse.message()).contains("name: Name is required");
    }

    @Test
    void testPostExerciseTemplate_returnsBadRequest_whenPrimaryMuscleGroupIsMissing() {
        ExerciseTemplate invalidTemplate = new ExerciseTemplate();
        invalidTemplate.setName("barbell squat");

        BindingResult bindingResult = new BeanPropertyBindingResult(invalidTemplate, "exerciseTemplate");
        bindingResult.rejectValue("primaryMuscleGroup", "NotBlank", "Primary muscle group is required");

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        GlobalExceptionHandler.ErrorResponse errorResponse = handler.handleValidationExceptions(exception);

        assertThat(errorResponse.status()).isEqualTo(400);
        assertThat(errorResponse.message()).contains("primaryMuscleGroup: Primary muscle group is required");
    }
}
