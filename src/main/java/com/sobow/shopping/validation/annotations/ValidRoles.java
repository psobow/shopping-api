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
 * <p>Each role value is normalized before comparison:
 * <ol>
 *   <li>{@code null} list → valid (combine with {@code @NotNull/@NotEmpty} if presence is required)</li>
 *   <li>for each item: reject if the item or its {@code authority} is {@code null}</li>
 *   <li>apply {@code strip()}, then convert to upper case using {@link java.util.Locale#ROOT}</li>
 *   <li>optionally remove a leading {@code "ROLE_"} prefix, if present</li>
 *   <li>check membership in the configured allow-list</li>
 * </ol>
 * The default allow-list is {@code {"USER","ADMIN"}}.</p>
 *
 * <h3>Supported targets</h3>
 * <ul>
 *   <li>{@link java.lang.annotation.ElementType#FIELD}</li>
 * </ul>
 *
 * <h3>Supported types</h3>
 * <ul>
 *   <li>{@code java.util.List<UserAuthorityRequest>}</li>
 * </ul>
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
