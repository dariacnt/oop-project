package org.example;

public class Admin extends Operator{
    private int clearanceLevel;

    public Admin(String name, String role, String email, String department, int clearanceLevel) {
        super(name, role, email, department);
        this.clearanceLevel = clearanceLevel;
    }
}