package org.example;

public class Alert {
    private AlertType type;
    private Severity severity;
    private String message;
    private String ipAddress;

    public Alert(AlertType type, Severity severity, String message, String ipAddress) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.ipAddress = ipAddress;
    }

    public String getMessage() {
        return message;
    }
    public Severity getSeverity() {
        return severity;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public AlertType getType() {
        return type;
    }
}
