package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import java.util.List;

import com.simulador.criaturas.utils.SimulationStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

// --- DTO Principal para o Horizonte ---
@Data
@NoArgsConstructor
public class HorizonDTO {

    @NotNull(message = "A lista de entidades não pode ser nula.")
    @Valid
    private List<HorizonEntityDTO> entities;

    @NotNull(message = "O guardião não pode ser nulo.")
    @Valid
    private GuardianDTO guardiao;

    @NotNull(message = "O status da simulação não pode ser nulo.")
    private SimulationStatus status;
}
