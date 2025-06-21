package com.simulador.criaturas.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User toDomain(UserEntity userEntity);

    UserEntity toEntity(User user);
}
