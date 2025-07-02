package com.simulador.criaturas.infrastructure.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.simulador.criaturas.infrastructure.adapter.out.persistence.entity.UserEntity;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    @Query("SELECT SUM(u.simulationsRun) FROM UserEntity u")
    Long sumTotalSimulationsRun();

    @Query("SELECT SUM(u.pontuation) FROM UserEntity u")
    Long sumTotalSuccesses();
}
