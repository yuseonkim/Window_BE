package org.example.maeum2_be.entity.domain;

public enum Role {
    ROLE_USER("User"),
    ROLE_BEGINNER("BEGINNER");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
