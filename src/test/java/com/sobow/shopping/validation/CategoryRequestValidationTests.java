package com.sobow.shopping.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.sobow.shopping.domain.category.CategoryRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CategoryRequestValidationTests {
    
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    private static Stream<Arguments> invalidCases() {
        return Stream.of(
            arguments(new CategoryRequest(null),
                      "name",
                      "is null"
            ),
            arguments(new CategoryRequest(""),
                      "name",
                      "is empty"
            ),
            arguments(new CategoryRequest("  "),
                      "name",
                      "is blank"
            )
        );
    }
    
    @ParameterizedTest(name = "{index}: Category field: {1} {2}")
    @MethodSource("invalidCases")
    public void shouldFailValidation(CategoryRequest request, String fieldNameWithInvalidValue, String reason) {
        var validationSet = validator.validate(request);
        assertThat(validationSet).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(fieldNameWithInvalidValue);
        });
    }
}
