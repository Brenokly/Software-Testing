package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import java.util.List;

import com.simulador.criaturas.utils.SimulationStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

// --- DTO Principal para o Horizonte ---
@Data
@NoArgsConstructor
public class HorizonDTO {

    private List<HorizonEntityDTO> entities;
    private GuardianDTO guardiao;
    private SimulationStatus status;
}
