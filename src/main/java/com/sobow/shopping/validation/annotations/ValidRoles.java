package com.sobow.shopping.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that each item in a list of role requests matches an allow-list of role names.
 *
 * <p>Role values are normalized using {@code trim().toUpperCase(Locale.ROOT)}
 * before comparison. The default allow-list is {@code {"USER","ADMIN"}}.</p>
 *
 * <h3>Supported targets</h3>
 * <ul>
 *   <li>{@code FIELD}</li>
 *   <li>{@code PARAMETER}</li>
 * </ul>
 *
 * <h3>Supported types</h3>
 * <ul>
 *   <li>{@code java.util.List<com.sobow.shopping.domain.user.requests.UserAuthorityRequest>}</li>
 * </ul>
 *
 * <h3>Null semantics</h3>
 * <p>The annotated value may be {@code null}. In that case the constraint is considered valid.
 * Combine with {@code @NotNull}/{@code @NotEmpty} to enforce presence.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * public record UserUpdateRequest(
 *     @ValidRoles(allowed = {"USER","ADMIN"})
 *     List<UserAuthorityRequest> authorities
 * ) {}
 * }</pre>
 *
 * @see ValidRolesValidator
 * @since 1.0
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRolesValidator.class)
public @interface ValidRoles {
    
    /**
     * The default validation message.
     */
    String message() default "invalid role(s)";
    
    /**
     * The allow-list of role names (normalized to upper case for comparison).
     */
    String[] allowed() default {"USER", "ADMIN"};
    
    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for clients of the Bean Validation API.
     */
    Class<? extends Payload>[] payload() default {};
}
