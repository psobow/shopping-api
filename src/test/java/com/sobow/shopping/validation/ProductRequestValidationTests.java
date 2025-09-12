package com.sobow.shopping.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.sobow.shopping.domain.requests.ProductCreateRequest;
import com.sobow.shopping.domain.requests.ProductUpdateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ProductRequestValidationTests {
    
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    private static final String EMPTY_STRING = "";
    private static final String BLANK_STRING = "  ";
    
    private static ProductCreateRequest valid() {
        return new ProductCreateRequest(
            "Valid name",
            "Valid brandName",
            new BigDecimal(10),
            10,
            "Valid description",
            1L
        );
    }
    
    private static Stream<Arguments> invalidCasesOnCreate() {
        return Stream.of(
            arguments(new ProductCreateRequest(
                          null, valid().brandName(), valid().price(),
                          valid().availableQuantity(), valid().description(),
                          valid().categoryId()),
                      "name",
                      "is null"
            ),
            arguments(new ProductCreateRequest(
                          EMPTY_STRING, valid().brandName(), valid().price(),
                          valid().availableQuantity(), valid().description(),
                          valid().categoryId()),
                      "name",
                      "is empty"
            ),
            arguments(new ProductCreateRequest(
                          BLANK_STRING, valid().brandName(), valid().price(),
                          valid().availableQuantity(), valid().description(),
                          valid().categoryId()),
                      "name",
                      "is blank"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), null,
                          valid().price(), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "brandName",
                      "is null"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), EMPTY_STRING,
                          valid().price(), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "brandName",
                      "is empty"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), BLANK_STRING,
                          valid().price(), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "brandName",
                      "is blank"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          null, valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "price",
                      "is null"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          new BigDecimal(0), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "price",
                      "is zero"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          new BigDecimal(-1), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "price",
                      "is negative"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), -1,
                          valid().description(), valid().categoryId()),
                      "availableQuantity",
                      "is negative"
            ),
            
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          null, valid().categoryId()),
                      "description",
                      "is null"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          EMPTY_STRING, valid().categoryId()),
                      "description",
                      "is empty"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          BLANK_STRING, valid().categoryId()),
                      "description",
                      "is blank"
            ),
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          valid().description(), null),
                      "categoryId",
                      "is null"
            ),
            
            arguments(new ProductCreateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          valid().description(), 0L),
                      "categoryId",
                      "is zero"
            )
        );
    }
    
    private static Stream<Arguments> invalidCasesOnUpdate() {
        return Stream.of(
            arguments(new ProductUpdateRequest(
                          EMPTY_STRING, valid().brandName(), valid().price(),
                          valid().availableQuantity(), valid().description(),
                          valid().categoryId()),
                      "name",
                      "is empty"
            ),
            arguments(new ProductUpdateRequest(
                          BLANK_STRING, valid().brandName(), valid().price(),
                          valid().availableQuantity(), valid().description(),
                          valid().categoryId()),
                      "name",
                      "is blank"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), EMPTY_STRING,
                          valid().price(), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "brandName",
                      "is empty"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), BLANK_STRING,
                          valid().price(), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "brandName",
                      "is blank"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          new BigDecimal(0), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "price",
                      "is zero"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          new BigDecimal(-1), valid().availableQuantity(),
                          valid().description(), valid().categoryId()),
                      "price",
                      "is negative"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), -1,
                          valid().description(), valid().categoryId()),
                      "availableQuantity",
                      "is negative"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          EMPTY_STRING, valid().categoryId()),
                      "description",
                      "is empty"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          BLANK_STRING, valid().categoryId()),
                      "description",
                      "is blank"
            ),
            arguments(new ProductUpdateRequest(
                          valid().name(), valid().brandName(),
                          valid().price(), valid().availableQuantity(),
                          valid().description(), 0L),
                      "categoryId",
                      "is zero"
            )
        );
    }
    
    @ParameterizedTest(name = "{index}: Product field: {1} {2}")
    @MethodSource("invalidCasesOnCreate")
    public void shouldFailValidationOnCreate(ProductCreateRequest request, String fieldNameWithInvalidValue, String reason) {
        Set<ConstraintViolation<ProductCreateRequest>> violationSet = validator.validate(request);
        assertThat(violationSet).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(fieldNameWithInvalidValue);
        });
    }
    
    @ParameterizedTest(name = "{index}: Product field: {1} {2}")
    @MethodSource("invalidCasesOnUpdate")
    public void shouldFailValidationOnUpdate(ProductUpdateRequest request, String fieldNameWithInvalidValue, String reason) {
        Set<ConstraintViolation<ProductUpdateRequest>> violationSet = validator.validate(request);
        assertThat(violationSet).anySatisfy(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo(fieldNameWithInvalidValue);
        });
    }
}
