package com.sobow.shopping.validation.annotations;

import com.sobow.shopping.domain.user.requests.UserAuthorityRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Validator for {@link ValidRoles}.
 *
 * <p>Each {@link UserAuthorityRequest#authority()} is stripped, upper-cased (ROOT),
 * and checked for membership in the configured allow-list.</p>
 */
public class ValidRolesValidator implements ConstraintValidator<ValidRoles, List<UserAuthorityRequest>> {
    
    private Set<String> allowed;
    
    @Override
    public void initialize(ValidRoles annotation) {
        this.allowed = Set.of(annotation.allowed());
        
        if (allowed.isEmpty()) {
            throw new IllegalStateException("@ValidRoles.allowed must not be empty");
        }
    }
    
    public boolean isValid(List<UserAuthorityRequest> value, ConstraintValidatorContext ctx) {
        if (value == null) return true;
        
        for (UserAuthorityRequest item : value) {
            if (item == null || item.authority() == null) return false;
            String normalized = item.authority().strip().toUpperCase(Locale.ROOT);
            if (!allowed.contains(normalized)) return false;
        }
        return true;
    }
}