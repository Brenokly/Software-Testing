package com.simulador.criaturas.integration.repositorys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.simulador.criaturas.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;

/**
 * Teste de Integração para a camada de persistência de Usuário
 * (UserRepository).
 *
 * A anotação @DataJpaTest é uma especialização do Spring Boot para testes
 * focados na camada de persistência JPA. Ela automatiza o roteiro de testes com
 * banco de dados, conforme descrito no documento de referência:
 *
 * Configuração do Ambiente: Ela configura um banco de dados em memória (como o
 * H2, similar ao HSQLDB do exemplo) e estabelece a conexão automaticamente,
 * eliminando a necessidade de gerenciamento manual.
 *
 * Foco e Escopo: Carrega apenas os componentes necessários para a camada JPA
 * (classes @Entity, como a UserEntity, e seus Repositórios), tornando o teste
 * mais rápido e focado que um teste de sistema completo.
 *
 * Isolamento dos Testes: Por padrão, cada método de teste é executado dentro de
 * uma transação que é revertida (rollback) ao final. Isso garante que o banco
 * de dados esteja sempre em um estado limpo antes de cada novo teste, cumprindo
 * o requisito de "limpar o BD" de forma automática, sem a necessidade de
 * comandos como truncate table.
 */
@DataJpaTest
class SpringDataUserRepositoryTest {

    @Autowired
    private SpringDataUserRepository userRepository;

    @Test
    @DisplayName("findByLogin: Deve encontrar um usuário quando o login existe")
    void findByLogin_shouldFindUser_whenLoginExists() {
        UserEntity user = new UserEntity(null, "testuser", "pass", 1, 10, 5);
        userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findByLogin("testuser");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getLogin()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("findByLogin: Deve retornar vazio quando o usuário não existe")
    void findByLogin_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<UserEntity> foundUser = userRepository.findByLogin("nonexistent");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("existsByLogin: Deve retornar true quando o login existe")
    void existsByLogin_shouldReturnTrue_whenLoginExists() {
        UserEntity user = new UserEntity(null, "existinguser", "pass", 1, 10, 5);
        userRepository.save(user);

        boolean exists = userRepository.existsByLogin("existinguser");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByLogin: Deve retornar false quando o login não existe")
    void existsByLogin_shouldReturnFalse_whenLoginDoesNotExist() {
        boolean exists = userRepository.existsByLogin("newuser");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("sumTotalSimulationsRun: Deve retornar a soma correta com múltiplos usuários")
    void sumTotalSimulationsRun_shouldReturnCorrectSum() {
        userRepository.save(new UserEntity(null, "user1", "p", 1, 10, 5));
        userRepository.save(new UserEntity(null, "user2", "p", 1, 2, 15));
        userRepository.save(new UserEntity(null, "user3", "p", 1, 8, 10));

        Long totalSimulations = userRepository.sumTotalSimulationsRun();

        assertThat(totalSimulations).isEqualTo(30L);
    }

    @Test
    @DisplayName("sumTotalSimulationsRun: Deve retornar null se a tabela estiver vazia")
    void sumTotalSimulationsRun_shouldReturnNull_whenTableIsEmpty() {
        Long totalSimulations = userRepository.sumTotalSimulationsRun();

        assertThat(totalSimulations).isNull();
    }

    @Test
    @DisplayName("sumTotalSuccesses: Deve retornar a soma correta de pontuações")
    void sumTotalSuccesses_shouldReturnCorrectSum() {
        userRepository.save(new UserEntity(null, "user1", "p", 1, 10, 5));
        userRepository.save(new UserEntity(null, "user2", "p", 1, 20, 15));

        Long totalSuccesses = userRepository.sumTotalSuccesses();

        assertThat(totalSuccesses).isEqualTo(30L);
    }

    @Test
    @DisplayName("sumTotalSuccesses: Deve retornar null se a tabela estiver vazia")
    void sumTotalSuccesses_shouldReturnNull_whenTableIsEmpty() {
        Long totalSuccesses = userRepository.sumTotalSuccesses();

        assertThat(totalSuccesses).isNull();
    }
}
