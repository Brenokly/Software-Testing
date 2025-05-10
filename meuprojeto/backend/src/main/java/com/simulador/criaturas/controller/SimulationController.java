package com.simulador.criaturas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.dtos.SimulationRequestDTO;
import com.simulador.criaturas.service.SimulationManager;
import com.simulador.criaturas.service.SimulationService;

@CrossOrigin
@RestController
@RequestMapping("/api/simulacao")
public class SimulationController {

  private final SimulationManager simulationManager;

  @Autowired
  public SimulationController(SimulationManager simulationManager) {
    this.simulationManager = simulationManager;
  }

  @PostMapping("/iniciar")
  public IterationStatusDTO iniciarSimulacao(@RequestBody SimulationRequestDTO request) {
    return simulationManager.iniciarSimulacao(request.getQuantidade());
  }

  @PostMapping("/iterar")
  public IterationStatusDTO iterar() {
    return getService().iterate();
  }

  @GetMapping("/criatura-atual")
  public int getCriaturaAtual() {
    return getService().creatureCurrent();
  }

  @PostMapping("/resetar")
  public void resetarSimulacao() {
    getService().resetSimulation();
  }

  private SimulationService getService() {
    return simulationManager.getSimulacao();
  }
}