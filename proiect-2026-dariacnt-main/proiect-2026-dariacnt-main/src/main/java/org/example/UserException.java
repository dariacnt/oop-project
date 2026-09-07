package org.example;

public class UserException extends Exception {
    public UserException() { super("UserException: Name and role can't be empty."); }
}
