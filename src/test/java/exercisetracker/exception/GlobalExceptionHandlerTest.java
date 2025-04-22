package exercisetracker.exception;

import exercisetracker.model.ExerciseTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {
    @Test
    void returnsBadRequest_whenNameIsMissing() {
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
    void returnsBadRequest_whenPrimaryMuscleGroupIsMissing() {
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
