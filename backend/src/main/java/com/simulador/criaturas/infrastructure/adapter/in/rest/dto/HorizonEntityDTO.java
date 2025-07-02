package com.simulador.criaturas.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

// --- DTO Base para todas as entidades do horizonte ---
@JsonTypeInfo( // Anotação MÁGICA para o Jackson lidar com polimorfismo
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // O JSON terá um campo "type": "CREATURE_UNIT", etc.
)
@JsonSubTypes({ // Mapeia o valor do campo "type" para a classe DTO correta

    @JsonSubTypes.Type(value = CreatureUnitDTO.class, name = "CREATURE_UNIT"),
    @JsonSubTypes.Type(value = CreatureClusterDTO.class, name = "CREATURE_CLUSTER")
})
@Data
public abstract class HorizonEntityDTO {

    private int id;
    private double x;
    private double gold;
}
