package com.simulador.criaturas.infrastructure.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserRequestDTO;
import com.simulador.criaturas.infrastructure.adapter.in.rest.dto.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converte um User do domínio para um DTO de resposta (seguro para enviar
     * ao frontend).
     */
    UserResponseDTO toResponseDto(User user);

    /**
     * Converte um DTO de requisição para um objeto User de domínio.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarId", ignore = true)
    @Mapping(target = "pontuation", ignore = true)
    @Mapping(target = "simulationsRun", ignore = true)
    User toDomain(UserRequestDTO userRequestDTO);
}
