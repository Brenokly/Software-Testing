package com.simulador.criaturas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimulationManager {

  private final SimulationService simulationService;

  @Autowired
  public SimulationManager(SimulationService simulationService) {
    this.simulationService = simulationService;
  }

  public void iniciarSimulacao(int quantidade) {
    simulationService.startSimulation(quantidade); // Usar o método startSimulation do SimulationService
  }

  public SimulationService getSimulacao() {
    if (simulationService == null) {
      throw new IllegalStateException("Simulação ainda não foi iniciada.");
    }
    return simulationService;
  }

  public boolean isSimulacaoIniciada() {
    return simulationService != null;
  }
}
