package com.bazuuyu.b2b.core.config;

public final class SecurityConstants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ROLE_CLAIM = "role";
    public static final String STATUS_CLAIM = "status";
    public static final String USER_ID_CLAIM = "userId";
    public static final String EMAIL_CLAIM = "email";

    private SecurityConstants() {
    }
}
