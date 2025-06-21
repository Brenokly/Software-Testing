package com.simulador.criaturas.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simulador.criaturas.infrastructure.adapter.out.persistence.entity.UserEntity;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    boolean existsByLogin(String login);

}
