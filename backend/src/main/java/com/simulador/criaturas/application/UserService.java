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
    public void deleteUser(Long userIdToDelete, String requesterLogin) {
        // Busca o usuário que se quer deletar
        User userToDelete = userRepository.findById(userIdToDelete)
                .orElseThrow(() -> new RuntimeException("Usuário a ser deletado não encontrado."));

        // Regra de negócio de segurança:
        // O login do usuário a ser deletado é o mesmo de quem fez a requisição?
        if (!userToDelete.getLogin().equals(requesterLogin)) {
            // Se não for, lançamos um erro de acesso negado.
            // Em um sistema real, usaríamos uma exceção customizada e um status 403 Forbidden.
            throw new SecurityException("Acesso negado: Você só pode deletar sua própria conta.");
        }

        // Se a verificação passar, a exclusão é permitida.
        userRepository.deleteById(userIdToDelete);
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public void incrementSimulationsRun(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para incrementar simulações."));

        // Incrementa o contador de simulações executadas
        user.incrementSimulationsRun();

        // Atualiza o usuário no repositório
        userRepository.update(user);
    }
}
