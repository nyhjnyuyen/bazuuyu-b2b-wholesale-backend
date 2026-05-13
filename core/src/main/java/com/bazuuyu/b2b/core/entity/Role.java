package com.bazuuyu.b2b.core.entity;

public enum Role {
    ADMIN,
    SALES_MANAGER,
    WHOLESALE_BUYER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
