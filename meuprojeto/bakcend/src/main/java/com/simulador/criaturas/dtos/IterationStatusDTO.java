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

  public static IterationStatusDTO toDTO(List<CreatureResponseDTO> statusCreatures,
      List<CreatureResponseDTO> inactiveCreatures, int iterationCount, boolean isFinished) {
    return new IterationStatusDTO(statusCreatures, inactiveCreatures, iterationCount, isFinished);
  }
}