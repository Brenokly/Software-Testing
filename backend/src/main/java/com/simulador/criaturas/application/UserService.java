package com.simulador.criaturas.application;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.simulador.criaturas.domain.model.User;
import com.simulador.criaturas.domain.port.in.UserUseCase;
import com.simulador.criaturas.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

/**
 * Serviço de Aplicação que implementa os casos de uso de usuário.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    // Depende da Porta de Saída para persistência
    private final UserRepositoryPort userRepository;
    // Depende de um codificador de senhas para segurança
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUser(String login, String password) {
        if (userRepository.existsByLogin(login)) {
            throw new RuntimeException("Erro: Login já está em uso.");
        }

        // Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(password);

        User novoUsuario = new User(login, senhaCriptografada);
        return userRepository.save(novoUsuario);
    }

    @Override
    public Optional<User> authenticateUser(String login, String password) {
        Optional<User> userOptional = userRepository.findByLogin(login);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Compara a senha enviada com a senha criptografada no banco
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty(); // Retorna vazio se o usuário não existe ou a senha está incorreta
    }

    @Override
    public void incrementScore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para incrementar pontuação."));

        // A lógica de negócio vive no modelo de domínio!
        user.incrementScore();

        userRepository.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuário não encontrado para exclusão.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
