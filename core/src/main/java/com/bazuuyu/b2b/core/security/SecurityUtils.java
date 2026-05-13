package com.bazuuyu.b2b.core.security;

import com.bazuuyu.b2b.core.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ForbiddenException("AUTHENTICATION_REQUIRED", "Authentication is required.");
        }
        return user;
    }

    public static String currentUsername() {
        return getAuthenticatedUser().username();
    }
}
