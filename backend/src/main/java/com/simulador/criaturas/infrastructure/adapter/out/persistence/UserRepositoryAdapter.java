package com.simulador.criaturas.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        UserEntity userEntity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(userEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jpaRepository.findByLogin(login).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(User user) {
        jpaRepository.delete(mapper.toEntity(user));
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> userEntities = jpaRepository.findAll();
        return userEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public long countTotalSimulations() {
        Long total = jpaRepository.sumTotalSimulationsRun();
        return total != null ? total : 0L;
    }

    @Override
    public long countTotalSuccesses() {
        Long total = jpaRepository.sumTotalSuccesses();
        return total != null ? total : 0L;
    }
}
