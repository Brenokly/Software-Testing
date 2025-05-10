package com.simulador.criaturas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simulador.criaturas.dtos.IterationStatusDTO;

@Component
public class SimulationManager {

  private final SimulationService simulationService;

  @Autowired
  public SimulationManager(SimulationService simulationService) {
    this.simulationService = simulationService;
  }

  public IterationStatusDTO iniciarSimulacao(int quantidade) {
    return simulationService.startSimulation(quantidade);
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
