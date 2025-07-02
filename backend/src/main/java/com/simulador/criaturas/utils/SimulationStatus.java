package com.simulador.criaturas.utils;

public enum SimulationStatus {
    RUNNING("Running"), // A simulação ainda está em andamento.
    SUCCESSFUL("Successful"), // A simulação terminou com sucesso.
    FAILED("Failed"); // A simulação terminou em um estado de falha/empate.

    private final String description;

    SimulationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static SimulationStatus fromString(String status) {
        for (SimulationStatus s : SimulationStatus.values()) {
            if (s.description.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
