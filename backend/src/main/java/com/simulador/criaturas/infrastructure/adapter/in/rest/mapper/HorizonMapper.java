package com.simulador.criaturas.infrastructure.adapter.in.rest.mapper;

import org.mapstruct.Mapper;

import com.simulador.criaturas.domain.behaviors.HorizonEntities;
import com.simulador.criaturas.domain.model.CreatureCluster;
import com.simulador.criaturas.domain.model.CreatureUnit;
import com.simulador.criaturas.domain.model.Guardian;
import com.simulador.criaturas.domain.model.Horizon;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.CreatureClusterDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.CreatureUnitDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.GuardianDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.HorizonDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.HorizonEntityDTO;

@Mapper(componentModel = "spring")
public interface HorizonMapper {

    // --- Conversões DTO -> DOMÍNIO ---
    Horizon toDomain(HorizonDTO dto);

    Guardian toDomain(GuardianDTO dto);

    CreatureUnit toDomain(CreatureUnitDTO dto);

    CreatureCluster toDomain(CreatureClusterDTO dto);

    // --- Conversões DOMÍNIO -> DTO ---
    HorizonDTO toDto(Horizon domain);

    GuardianDTO toDto(Guardian domain);

    CreatureUnitDTO toDto(CreatureUnit domain);

    CreatureClusterDTO toDto(CreatureCluster domain);

    // --- LÓGICA POLIMÓRFICA CUSTOMIZADA ---
    // Este método ensina o MapStruct a converter do Domínio para o DTO correto
    default HorizonEntityDTO toEntityDto(HorizonEntities entity) {
        if (entity instanceof CreatureUnit creatureUnit) {
            return toDto(creatureUnit);
        }
        if (entity instanceof CreatureCluster creatureCluster) {
            return toDto(creatureCluster);
        }
        throw new IllegalArgumentException("Tipo de entidade de domínio desconhecido: ");
    }

    // ESTE NOVO MÉTODO ENSINA O MAPSTRUCT A FAZER O CAMINHO INVERSO
    default HorizonEntities toEntityDomain(HorizonEntityDTO dto) {
        if (dto instanceof CreatureUnitDTO creatureUnitDTO) {
            return toDomain(creatureUnitDTO);
        }
        if (dto instanceof CreatureClusterDTO creatureClusterDTO) {
            return toDomain(creatureClusterDTO);
        }
        throw new IllegalArgumentException("Tipo de DTO de entidade desconhecido: ");
    }
}
