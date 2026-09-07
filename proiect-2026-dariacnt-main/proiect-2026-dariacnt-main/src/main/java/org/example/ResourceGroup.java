package org.example;

import java.io.PrintWriter;
import java.util.*;

public class ResourceGroup {
    private List<User> members = new ArrayList<>();
    private String ipAddress;

    // Observer
    private Server server;

    public ResourceGroup(String ipAddress, Server server) {
        this.ipAddress = ipAddress;
        this.server = server;

        if (this.server != null) {
            this.server.addObserver(this);
        }
    }

    public void update(Alert alert, PrintWriter pw) {
        // daca se doreste testarea outputului:
        // pw.println("Grupul a primit alerta: " + alerta.getMessage());
    }

    public void addMember(User user){
        members.add(user);
    }

    public List<User> getMembers(){
        return members;
    }

    public void removeMember(User user){
        members.remove(user);
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
