package com.simulador.criaturas.infrastructure.adapter.in.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationRequestDTO {

    @Min(value = 2, message = "A quantidade mínima de criaturas é 2.")
    @Max(value = 10, message = "A quantidade máxima de criaturas é 10.")
    private int quantidade;
}