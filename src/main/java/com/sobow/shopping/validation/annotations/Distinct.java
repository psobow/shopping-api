package com.sobow.shopping.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ensures that a {@link java.util.Collection Collection} contains no duplicate elements.
 *
 * <p>Duplicates are determined using the element type's {@link Object#equals(Object)}
 * and {@link Object#hashCode()} implementations.</p>
 *
 * <h3>Supported targets</h3>
 * <ul>
 *   <li>{@code FIELD}</li>
 *   <li>{@code PARAMETER}</li>
 * </ul>
 *
 * <h3>Supported types</h3>
 * <ul>
 *   <li>{@code Collection<?>} (e.g. {@code List}, {@code Set})</li>
 * </ul>
 *
 * <h3>Null semantics</h3>
 * <p>The annotated value may be {@code null}. In that case the constraint
 * is considered valid. Combine with {@code @NotNull}/{@code @NotEmpty} if
 * you want to enforce presence.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * public record UserUpdateRequest(
 *     @Distinct
 *     List<UserAuthorityRequest> authorities
 * ) {}
 * }</pre>
 *
 * @see DistinctValidator
 * @since 1.0
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DistinctValidator.class)
public @interface Distinct {
    
    /**
     * The default validation message.
     */
    String message() default "list contains duplicates";
    
    /**
     * Allows specification of validation groups, per Bean Validation spec.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for clients of the Bean Validation API.
     */
    Class<? extends Payload>[] payload() default {};
}