package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// --- DTO para o Guardião ---
@Data
@NoArgsConstructor
public class GuardianDTO {

    private int id;
    private double x;
    private double gold;
}
