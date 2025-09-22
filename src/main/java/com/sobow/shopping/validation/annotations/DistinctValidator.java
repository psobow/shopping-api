package com.sobow.shopping.validation.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.HashSet;

/**
 * Validator for {@link Distinct}.
 *
 * <p>Returns {@code true} if the value is {@code null}, or if the number of
 * elements equals the number of distinct elements according to {@code equals/hashCode}.</p>
 */
public class DistinctValidator implements ConstraintValidator<Distinct, Collection<?>> {
    
    public boolean isValid(Collection<?> value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        return value.size() == new HashSet<>(value).size();
    }
}