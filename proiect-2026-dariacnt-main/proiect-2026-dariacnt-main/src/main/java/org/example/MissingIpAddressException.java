package org.example;

public class MissingIpAddressException extends Exception {
    public MissingIpAddressException() { super("MissingIpAddressException: Server IP Address was not provided."); }
}
