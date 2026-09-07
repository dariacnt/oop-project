package org.example;

// Factory
public class UserFactory {
    public User createUser(String name, String role, String email, String department, String clearanceStr) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return new Admin(name, role, email, department, Integer.parseInt(clearanceStr));

            case "OPERATOR":
                return new Operator(name, role, email, department);

            default:
                return new User(name, role, email);
        }
    }
}
