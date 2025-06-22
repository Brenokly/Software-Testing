package com.simulador.criaturas.application;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // Helper para checar strings

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public User registerNewUser(String login, String password, int avatarId) {
        // Validação para cumprir o contrato.
        if (!StringUtils.hasText(login)) {
            throw new IllegalArgumentException("Login não pode ser nulo ou vazio.");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia.");
        }
        if (userRepository.existsByLogin(login)) {
            // Lançando a exceção correta, conforme o contrato.
            throw new IllegalArgumentException("Erro: Login já está em uso.");
        }

        String senhaCriptografada = passwordEncoder.encode(password);
        User novoUsuario = new User(login, senhaCriptografada, avatarId);
        return userRepository.save(novoUsuario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> authenticateUser(String login, String password) {
        // Validação para cumprir o contrato.
        if (login == null) {
            throw new IllegalArgumentException("Login não pode ser nulo.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Senha não pode ser nula.");
        }

        return userRepository.findByLogin(login)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementScore(Long userId) {
        User user = findUserByIdOrThrow(userId);
        user.incrementScore();
        userRepository.update(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userIdToDelete, String requesterLogin) {
        User userToDelete = findUserByIdOrThrow(userIdToDelete);

        // Validação do parâmetro do caso de uso.
        if (!StringUtils.hasText(requesterLogin)) {
            throw new IllegalArgumentException("Login do solicitante não pode ser nulo ou vazio.");
        }

        if (!userToDelete.getLogin().equals(requesterLogin)) {
            // Lançando a exceção correta, conforme o contrato.
            throw new SecurityException("Acesso negado: Você só pode deletar sua própria conta.");
        }

        userRepository.deleteById(userIdToDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findUserByLogin(String login) {
        // Validação para cumprir o contrato.
        if (login == null) {
            throw new IllegalArgumentException("Login não pode ser nulo.");
        }
        return userRepository.findByLogin(login);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incrementSimulationsRun(Long userId) {
        User user = findUserByIdOrThrow(userId);
        user.incrementSimulationsRun();
        userRepository.update(user);
    }

    /**
     * Método auxiliar privado para buscar um usuário pelo ID, garantindo que a
     * exceção correta do contrato seja lançada se o usuário não for encontrado.
     *
     * @param userId O ID do usuário a ser buscado.
     * @return O objeto User encontrado.
     * @throws IllegalArgumentException Se o ID for nulo ou se nenhum usuário
     * for encontrado com o ID fornecido.
     */
    private User findUserByIdOrThrow(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + userId + " não encontrado."));
    }
}
