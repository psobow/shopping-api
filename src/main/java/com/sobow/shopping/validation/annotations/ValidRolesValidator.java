package com.sobow.shopping.validation.annotations;

import com.sobow.shopping.domain.user.requests.admin.AuthorityDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Validator for {@link ValidRoles}.
 *
 * <p>For each {@link AuthorityDto} in the list:
 * <ul>
 *   <li>returns {@code true} if the list itself is {@code null};</li>
 *   <li>returns {@code false} if an item or its {@code authority} is {@code null};</li>
 *   <li>normalizes the value by applying {@code strip()}, converting to upper case with
 *       {@link java.util.Locale#ROOT}, and removing a leading {@code "ROLE_"} prefix if present;</li>
 *   <li>considers the value valid only if it is contained in the configured allow-list.</li>
 * </ul>
 * The allow-list is taken from {@link ValidRoles#allowed()} and must not be empty.</p>
 */
public class ValidRolesValidator implements ConstraintValidator<ValidRoles, List<AuthorityDto>> {
    
    private Set<String> allowed;
    
    @Override
    public void initialize(ValidRoles annotation) {
        this.allowed = Set.of(annotation.allowed());
        
        if (allowed.isEmpty()) {
            throw new IllegalStateException("@ValidRoles.allowed must not be empty");
        }
    }
    
    public boolean isValid(List<AuthorityDto> value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        
        for (AuthorityDto item : value) {
            if (item == null || item.value() == null) return false;
            String normalized = item.value().strip().toUpperCase(Locale.ROOT);
            if (normalized.startsWith("ROLE_")) normalized = normalized.substring(5);
            if (!allowed.contains(normalized)) return false;
        }
        return true;
    }
}