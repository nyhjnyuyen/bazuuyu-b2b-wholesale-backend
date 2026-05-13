package com.bazuuyu.b2b.core.dto;

import com.bazuuyu.b2b.core.entity.Role;
import com.bazuuyu.b2b.core.enums.UserAccountStatus;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String username,
        String email,
        Role role,
        UserAccountStatus accountStatus
) {
}
