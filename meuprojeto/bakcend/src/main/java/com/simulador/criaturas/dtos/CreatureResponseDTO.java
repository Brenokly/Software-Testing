package com.simulador.criaturas.dtos;

import java.util.List;
import java.util.stream.Collectors;

import com.simulador.criaturas.model.Creature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para a entidade Creature. Usado para expor apenas os dados
 * necessários em respostas da API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatureResponseDTO {

    private int id;         // ID da criatura
    private double x;       // Posição da criatura no eixo X (Horizonte)
    private double gold;    // Quantidade de ouro da criatura

    public static CreatureResponseDTO toDTO(Creature creature) {
        return new CreatureResponseDTO(
                creature.getId(),
                creature.getX(),
                creature.getGold());
    }

    public static List<CreatureResponseDTO> toDTOList(List<Creature> creatures) {
        return creatures.stream()
                .map(CreatureResponseDTO::toDTO)
                .collect(Collectors.toList());
    }
}
