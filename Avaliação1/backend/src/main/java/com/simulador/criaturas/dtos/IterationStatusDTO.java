package com.simulador.criaturas.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IterationStatusDTO {
  private List<CreatureResponseDTO> statusCreatures;
  private List<CreatureResponseDTO> inactiveCreatures;
  private int iterationCount;
  private boolean isFinished;

  /**
   * Cria um objeto IterationStatusDTO a partir de listas de criaturas ativas e
   * inativas.
   *
   * @param statusCreatures   Lista de criaturas ativas.
   * @param inactiveCreatures Lista de criaturas inativas.
   * @param iterationCount    Contador de iterações.
   * @param isFinished        Indica se a simulação foi finalizada.
   * @return Um objeto IterationStatusDTO contendo as informações fornecidas.
   * @pre Nenhuma pré-condição específica.
   * @post O objeto IterationStatusDTO será criado com os valores fornecidos.
   * @throws Exception Nenhuma exceção é lançada pelo método.
   */
  public static IterationStatusDTO toDTO(List<CreatureResponseDTO> statusCreatures,
      List<CreatureResponseDTO> inactiveCreatures, int iterationCount, boolean isFinished) {
    return new IterationStatusDTO(statusCreatures, inactiveCreatures, iterationCount, isFinished);
  }
}