package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Server {
    // obligatorii 
    private String ipAddress;
    private Location location;
    private User owner;

    // optionale
    private String hostname;
    private ServerStatus status;
    private Integer cpuCores;
    private Integer ramGb;
    private Integer storageGb;

    private Alert ultimaAlerta;
    private List<ResourceGroup> observatori = new ArrayList<>();

    private Server(Builder builder) {
        this.ipAddress = builder.ipAddress;
        this.location = builder.location;
        this.owner = builder.owner;
        this.hostname = builder.hostname;
        this.status = builder.status;
        this.cpuCores = builder.cpuCores;
        this.ramGb = builder.ramGb;
        this.storageGb = builder.storageGb;
    }

    // Observer
    public Alert getStare() {
        return ultimaAlerta;
    }

    public void genereazaAlerta(Alert alerta, PrintWriter out) {
        this.ultimaAlerta = alerta;
        notificaObservatori(alerta, out);
    }

    public void addObserver(ResourceGroup observator) {
        observatori.add(observator);
    }

    private void notificaObservatori(Alert alerta, PrintWriter out) {
        for (ResourceGroup obs : observatori) {
            obs.update(alerta, out);
        }
    }

    public String getIpAddress() { return ipAddress; }

    public String getStatus() {
        return status.toString();
    }

    // Builder
    public static class Builder {
        private String ipAddress;
        private Location location;
        private User owner;

        private String hostname;
        private ServerStatus status;
        private Integer cpuCores;
        private Integer ramGb;
        private Integer storageGb;

        public Builder(String ip, Location loc, User owner) {
            this.ipAddress = ip;
            this.location = loc;
            this.owner = owner;
        }

        public Builder setHostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder setStatus(ServerStatus status) {
            this.status = status;
            return this;
        }

        public Builder setCpuCores(Integer cpu) {
            this.cpuCores = cpu;
            return this;
        }
        public Builder setRamGb(Integer ram) {
            this.ramGb = ram;
            return this;
        }
        public Builder setStorageGb(Integer storage) {
            this.storageGb = storage;
            return this;
        }

        public Server build() {
            return new Server(this);
        }
    }
}