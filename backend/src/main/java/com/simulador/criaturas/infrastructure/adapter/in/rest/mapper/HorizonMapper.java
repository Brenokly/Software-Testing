package com.simulador.criaturas.infrastructure.adapter.in.rest.mapper;

import java.util.List;
import java.util.stream.Collectors;

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

    // Métodos de conversão principais
    HorizonDTO toDto(Horizon horizon);

    Horizon toDomain(HorizonDTO horizonDTO);

    // Métodos auxiliares para as entidades internas
    GuardianDTO toDto(Guardian guardian);

    Guardian toDomain(GuardianDTO guardianDTO);

    CreatureUnitDTO toDto(CreatureUnit creatureUnit);

    CreatureUnit toDomain(CreatureUnitDTO creatureUnitDTO);

    CreatureClusterDTO toDto(CreatureCluster creatureCluster);

    CreatureCluster toDomain(CreatureClusterDTO creatureClusterDTO);

    // Método customizado para lidar com a lista polimórfica
    default List<HorizonEntityDTO> toEntityDtoList(List<HorizonEntities> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::toEntityDto).collect(Collectors.toList());
    }

    default HorizonEntityDTO toEntityDto(HorizonEntities entity) {
        if (entity instanceof CreatureUnit creatureUnit) {
            return toDto(creatureUnit);
        }
        if (entity instanceof CreatureCluster creatureCluster) {
            return toDto(creatureCluster);
        }

        if (entity != null) {
            throw new IllegalArgumentException("Tipo de entidade desconhecido: " + entity.getClass());
        }

        return null;
    }
}
