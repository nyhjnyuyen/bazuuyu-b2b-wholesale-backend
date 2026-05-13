package com.bazuuyu.b2b.core.security;

import com.bazuuyu.b2b.core.entity.Role;
import com.bazuuyu.b2b.core.enums.UserAccountStatus;

public record AuthenticatedUser(
        Long userId,
        String username,
        String email,
        Role role,
        UserAccountStatus accountStatus
) {
}
