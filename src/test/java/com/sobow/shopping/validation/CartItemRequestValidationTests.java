package com.sobow.shopping.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.sobow.shopping.domain.requests.CartItemCreateRequest;
import com.sobow.shopping.domain.requests.CartItemUpdateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CartItemRequestValidationTests {
    
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    private static Stream<Arguments> invalidCasesOnCreate() {
        return Stream.of(
            arguments(new CartItemCreateRequest(0L, 1),
                      "productId",
                      "is not positive"
            ),
            arguments(new CartItemCreateRequest(1L, 0),
                      "requestedQty",
                      "is not positive"
            )
        );
    }
    
    private static Stream<Arguments> invalidCasesOnUpdate() {
        return Stream.of(
            arguments(new CartItemUpdateRequest(0),
                      "requestedQty",
                      "is not positive"
            )
        );
    }
    
    @ParameterizedTest(name = "{index}: Cart item field: {1} {2}")
    @MethodSource("invalidCasesOnCreate")
    public void shouldFailValidationOnCreate(CartItemCreateRequest request, String fieldNameWithInvalidValue, String reason) {
        var validationSet = validator.validate(request);
        assertThat(validationSet).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(fieldNameWithInvalidValue);
        });
    }
    
    @ParameterizedTest(name = "{index}: Cart item field: {1} {2}")
    @MethodSource("invalidCasesOnUpdate")
    public void shouldFailValidationOnUpdate(CartItemUpdateRequest request, String fieldNameWithInvalidValue, String reason) {
        var validationSet = validator.validate(request);
        assertThat(validationSet).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(fieldNameWithInvalidValue);
        });
    }
}
