package com.simulador.criaturas.domain.port.in;

import java.util.Optional;

import com.simulador.criaturas.domain.model.User;

/**
 * Porta de Entrada que define os Casos de Uso para o gerenciamento de usuários.
 */
public interface UserUseCase {

    /**
     * Registra um novo usuário no sistema.
     *
     * @param login O login do novo usuário.
     * @param password A senha em texto puro do novo usuário.
     * @return O objeto User criado e salvo.
     * @throws RuntimeException se o login já existir.
     */
    User registerNewUser(String login, String password);

    /**
     * Autentica um usuário com login e senha.
     *
     * @param login O login do usuário.
     * @param password A senha em texto puro para verificação.
     * @return um Optional contendo o User se a autenticação for bem-sucedida.
     */
    Optional<User> authenticateUser(String login, String password);

    /**
     * Incrementa a pontuação de um usuário.
     *
     * @param userId O ID do usuário a ter a pontuação incrementada.
     */
    void incrementScore(Long userId);

    /**
     * Exclui um usuário do sistema.
     *
     * @param userId O ID do usuário a ser excluído.
     */
    void deleteUser(Long userId);

    /**
     * Busca um usuário pelo seu login.
     *
     * @param login O login do usuário.
     * @return um Optional contendo o User se encontrado.
     */
    Optional<User> findUserByLogin(String login);

}
