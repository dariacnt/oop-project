package org.example;

public class Main {
    public static void main(String[] args) {

        if(args.length < 2)
            return;

        Database dataBase = Database.getInstance();
        String firstArg = args[0];

        PathTypes pathType;
        try {
            pathType = PathTypes.valueOf(firstArg.toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        if (args.length == 2) {
            String filePath = args[1];
            if (pathType == PathTypes.SERVERS) {
                dataBase.processServers(filePath);
            } else if (pathType == PathTypes.GROUPS) {
                dataBase.processGroups(filePath);
            }
        } else if (args.length == 4) {
            String serversPath = args[1];
            String groupsPath = args[2];
            String listenersPath = args[3];

            dataBase.processServers(serversPath);
            dataBase.processGroups(groupsPath);
            dataBase.processListeners(listenersPath);
        }
    }
}