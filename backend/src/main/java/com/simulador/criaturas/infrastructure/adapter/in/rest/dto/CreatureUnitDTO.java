package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// --- DTO para CreatureUnit ---
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreatureUnitDTO extends HorizonEntityDTO {
    // Não precisa de campos extras, herda tudo do base.
}
