package org.example.maeum2_be.entity.domain;

public enum Role {
    User("User"),
    NotUser("NotUser");

    String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
