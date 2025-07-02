package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// --- DTO para CreatureCluster ---
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreatureClusterDTO extends HorizonEntityDTO {
    // Clusters podem ter propriedades específicas no futuro, se necessário.
}
