package org.example;

import java.io.*;
import java.util.*;

public class Database {
    private static Database instance;
    private Set<Server> servers;
    private Set<ResourceGroup> resourceGroups;
    private Set<Alert> alerts;

    private Database() {
        this.servers = new LinkedHashSet<>();
        this.resourceGroups = new LinkedHashSet<>();
        this.alerts = new LinkedHashSet<>();
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void addServers(List<Server> serversList) {
        for (Server server : serversList) {
            servers.add(server);
        }
    }

    public void addResourceGroup(ResourceGroup resourceGroup) {
        resourceGroups.add(resourceGroup);
    }

    public void addResourceGroups(List<ResourceGroup> groupsList) {
        for (ResourceGroup resourceGroup : groupsList) {
            resourceGroups.add(resourceGroup);
        }
    }

    public void addAlert(Alert alert) {
        alerts.add(alert);
    }

    public Server findServer(String ip) {
        for (Server s : servers) {
            if (s.getIpAddress().equals(ip))
                return s;
        }
        return null;
    }

    public ResourceGroup findGroup(String ip) {
        for (ResourceGroup r : resourceGroups) {
            if (r.getIpAddress().equals(ip))
                return r;
        }
        return null;
    }

    public void removeResourceGroup(ResourceGroup group) {
        resourceGroups.remove(group);
    }

    public Set<Alert> getAlerts() {
        return alerts;
    }
    public Set<Server> getServers() {
        return servers;
    }
    public Set<ResourceGroup> getResourceGroups() {
        return resourceGroups;
    }

    public void processServers(String inputPath) {
        processFile(inputPath, PathTypes.SERVERS);
    }

    public void processGroups(String inputPath) {
        processFile(inputPath, PathTypes.GROUPS);
    }

    public void processListeners(String inputPath) {
        processFile(inputPath, PathTypes.LISTENER);
    }

    private void processFile(String inputPath, PathTypes type) {

        File file = new File(inputPath);
        if (!file.exists() && new File(inputPath + ".in").exists()) {
            inputPath += ".in";
        }
        
        String outputPath = "";
        if (inputPath.endsWith(".in")) {
            outputPath = inputPath.replace(".in", ".out");
        } else {
            outputPath = inputPath + ".out";
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
             PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {

            // ignoram prima linie
            br.readLine();
            int lineCnt = 1;
            String line;

            // citim fiecare linie cu comenzi
            while ((line = br.readLine()) != null) {
                String[] params = line.split("\\|", -1);
                getCommand(type, params, lineCnt, pw);
                lineCnt++;
            }

        } catch (IOException e) {
           System.err.println("Eroare la citire " + inputPath);
        }
    }

    private void getCommand(PathTypes type, String[] params, int lineCnt, PrintWriter pw) {
        try {
            switch (type) {
                case SERVERS:
                    addServer(params, pw);
                    break;
                case GROUPS:
                    String command = params[0];
                    switch (command) {
                        case "ADD GROUP": addGroup(params, pw);
                            break;
                        case "FIND GROUP": findGroup(params, pw);
                            break;
                        case "REMOVE GROUP": removeGroup(params, pw);
                            break;
                        case "ADD MEMBER": addMember(params, pw);
                            break;
                        case "FIND MEMBER": findMember(params, pw);
                            break;
                        case "REMOVE MEMBER": removeMember(params, pw);
                            break;
                    }
                    break;
                case LISTENER:
                    addEvent(params, pw);
                    break;
            }
        } catch (Exception e) {
            String command = params[0];
            pw.println(command + ": " + e.getMessage() + " ## line no: " + lineCnt);
        }
    }

    private void addServer(String[] p, PrintWriter pw) throws Exception {
        String hostname = p[1];
        String ip = p[2];
        if (ip.isEmpty()) 
            throw new MissingIpAddressException();
        
        String statusStr = p[3];
        String country = p[4];
        if (country.isEmpty()) 
            throw new LocationException();
        
        String city = p[5];
        String address = p[6];
        String latitudeStr = p[7];
        String longitudeStr = p[8];
        
        String username = p[9];
        String userRole = p[10];
        if (username.isEmpty() || userRole.isEmpty()) 
            throw new UserException();
        
        String email = p[11];
        String department = p[12];
        String clearance = p[13];

        Integer cpu = null;
        if (!p[14].isEmpty()) {
            cpu = Integer.parseInt(p[14]);
        }
        Integer ram = null;
        if (!p[15].isEmpty()) {
            ram = Integer.parseInt(p[15]);
        }

        Integer storage = null;
        if (!p[16].isEmpty()) {
            storage = Integer.parseInt(p[16]);
        }

        UserFactory userFactory = new UserFactory();
        User owner = userFactory.createUser(username, userRole, email, department, clearance);

        Location.Builder locBuilder = new Location.Builder(country)
                .setCity(city)
                .setAddress(address);

        if (!latitudeStr.isEmpty() && !longitudeStr.isEmpty()) {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            locBuilder.setLatitude(latitude).setLongitude(longitude);
        }

        Location location = locBuilder.build();
        ServerStatus status;
        try {
            status = ServerStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            status = ServerStatus.UP;
        }

        Server server = new Server.Builder(ip, location, owner).setHostname(hostname)
                .setStatus(status).setCpuCores(cpu).setRamGb(ram)
                .setStorageGb(storage)
                .build();

        addServer(server);
        pw.println("ADD SERVER: " + ip + ": " + server.getStatus());
    }

    private void addGroup(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty()) 
            throw new MissingIpAddressException();

        pw.println("ADD GROUP: " + ip);

        Server server = findServer(ip);
        ResourceGroup group = new ResourceGroup(ip, server);
        addResourceGroup(group);
    }

    private void findGroup(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty()) 
            throw new MissingIpAddressException();

        ResourceGroup group = findGroup(ip);
        if (group != null) {
            pw.println("FIND GROUP: " + ip);
        } else {
            pw.println("FIND GROUP: Group not found: ipAddress = " + ip);
        }
    }

    private void removeGroup(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty())
            throw new MissingIpAddressException();

        ResourceGroup group = findGroup(ip);
        if (group != null) {
            removeResourceGroup(group);
            pw.println("REMOVE GROUP: " + ip);
        } else {
            pw.println("REMOVE GROUP: Group not found: ipAddress = " + ip);
        }
    }

    private void addMember(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty())
            throw new MissingIpAddressException();

        String name = p[2];
        String role = p[3];
        if (name.isEmpty() || role.isEmpty())
            throw new UserException();

        ResourceGroup group = findGroup(ip);
        if (group != null) {
            String email = p[4];
            String department = p[5];
            String clearance = p[6];
            UserFactory userFactory = new UserFactory();
            User newUser = userFactory.createUser(name, role, email, department, clearance);
            group.addMember(newUser);
            pw.println("ADD MEMBER: " + ip + ": name = " + name + " && role = " + role);
        } else {
            pw.println("ADD MEMBER: Group not found: ipAddress = " + ip);
        }
    }

    private void findMember(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty())
            throw new MissingIpAddressException();

        String name = p[2];
        String role = p[3];
        if (name.isEmpty() || role.isEmpty())
            throw new UserException();

        ResourceGroup group = findGroup(ip);
        if (group == null) {
            pw.println("FIND MEMBER: Group not found: ipAddress = " + ip);
            return;
        }

        boolean ok = false;
        for (User u : group.getMembers()) {
            if (u.getName().matches(name) && u.getRole().matches(role)) {
                ok = true;
                break;
            }
        }

        if (ok) {
            pw.println("FIND MEMBER: " + ip + ": name = " + name + " && role = " + role);
        } else {
            pw.println("FIND MEMBER: Member not found: ipAddress = " + ip + ": name = " + name + " && role = " + role);
        }
    }

    private void removeMember(String[] p, PrintWriter pw) throws Exception {
        String ip = p[1];
        if (ip.isEmpty())
            throw new MissingIpAddressException();

        String name = p[2];
        String role = p[3];
        if (name.isEmpty() || role.isEmpty())
            throw new UserException();

        ResourceGroup group = findGroup(ip);
        if (group == null) {
            pw.println("REMOVE MEMBER: Group not found: ipAddress = " + ip);
            return;
        }

        User removeUser = null;
        for (User u : group.getMembers()) {
            if (u.getName().matches(name) && u.getRole().matches(role)) {
                removeUser = u;
                break;
            }
        }

        if (removeUser != null) {
            group.removeMember(removeUser);
            pw.println("REMOVE MEMBER: " + ip + ": name = " + name + " && role = " + role);
        } else {
            pw.println("REMOVE MEMBER: Member not found: ipAddress = " + ip + ": name = " + name + " && role = " + role);
        }
    }

    private void addEvent(String[] p, PrintWriter pw) throws Exception {
        AlertType type = AlertType.valueOf(p[1]);
        Severity severity = Severity.valueOf(p[2]);
        String message = p[4];
        String ip = p[3];
        if (ip.isEmpty()) return;

        pw.println("ADD EVENT: " + ip + ": type = " + type.toString() + " && severity = " + severity.toString() + " && message = " + message);

        Server server = findServer(ip);
        if (server != null) {
            Alert alert = new Alert(type, severity, message, ip);
            addAlert(alert);

            server.genereazaAlerta(alert, pw);
        }
    }
}