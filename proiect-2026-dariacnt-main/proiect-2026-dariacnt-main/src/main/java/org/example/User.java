package org.example;

public class User {
    private String name;
    private String role;
    private String email;

    public User(String name, String role, String email) {
        this.name = name;
        this.role = role;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public String getRole() {
        return role;
    }
    public String getEmail() {
        return email;
    }
}
