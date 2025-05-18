package com.simulador.criaturas.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simulador.criaturas.dtos.IterationStatusDTO;
import com.simulador.criaturas.exception.InsufficientCreatures;

/**
 * Gerencia o ciclo de vida da simulação e delega operações ao serviço de
 * simulação.
 * Garante que o serviço de simulação esteja sempre disponível após a
 * construção.
 */
@Component
public class SimulationManager {

  private final SimulationService simulationService;

  /**
   * Construtor que injeta o serviço de simulação.
   *
   * @param simulationService o serviço principal de simulação
   * @pre simulationService != null
   * @post A instância será criada com um serviço de simulação válido.
   * @throws NullPointerException se simulationService for null
   */
  @Autowired
  public SimulationManager(SimulationService simulationService) {
    this.simulationService = Objects.requireNonNull(simulationService, "SimulationService não pode ser null");
  }

  /**
   * Inicia uma nova simulação com a quantidade de criaturas informada.
   *
   * @param quantidade a quantidade de criaturas a participar da simulação
   * @return o estado atual da simulação após a inicialização
   * @pre quantidade > 0 (verificado internamente no SimulationService)
   * @post A simulação é iniciada e o estado correspondente é retornado.
   * @throws IllegalArgumentException se a quantidade for inválida (lançada por
   *                                  SimulationService)
   * @throws InsufficientCreatures    se não houver criaturas suficientes para
   *                                  iniciar a simulação
   */
  public IterationStatusDTO iniciarSimulacao(int quantidade) {
    return simulationService.startSimulation(quantidade);
  }

  /**
   * Retorna o serviço interno responsável pela simulação.
   *
   * @return o SimulationService utilizado por este gerenciador
   * @pre Nenhuma
   * @post Sempre retorna uma instância válida de SimulationService
   * @throws Nenhuma exceção é lançada pelo método.
   */
  public SimulationService getSimulacao() {
    return simulationService;
  }
}
