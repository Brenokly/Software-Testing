package com.simulador.criaturas.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.simulador.criaturas.domain.model.User;

/*
 * UserRepositoryPort.java
 *
 * Essa interface define as operações de persistência para o usuário.
 * Ela é a forma de o domínio expressar suas necessidades de persistência
 * sem depender de detalhes de implementação, como JPA ou banco de dados.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByLogin(String login);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    void delete(User user);

    void update(User user);

    boolean existsByLogin(String login);

    boolean existsById(Long id);

    List<User> findAll();
}
