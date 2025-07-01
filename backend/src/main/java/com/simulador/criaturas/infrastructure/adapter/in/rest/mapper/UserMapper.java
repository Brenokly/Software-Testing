package com.simulador.criaturas.infrastructure.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserRequestDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDto(User user);

    /**
     * Converte um DTO de requisição para um objeto User de domínio. Ignoramos
     * explicitamente os campos que não vêm da requisição e que devem ser
     * inicializados com valores padrão (0 ou null).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarId", ignore = true)
    @Mapping(target = "pontuation", ignore = true)
    @Mapping(target = "simulationsRun", ignore = true)
    User toDomain(UserRequestDTO userRequestDTO);
}
