package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AmountCreatures {

    @Min(value = 1, message = "A quantidade mínima é 1")
    @Max(value = 10, message = "A quantidade máxima é 10")
    private int amount;
}
