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
     * @param avatarId O ID do avatar do usuário.
     * @return O objeto User criado e salvo.
     * @throws IllegalArgumentException Se o login já existir ou se os
     * parâmetros forem inválidos (nulos ou vazios).
     * @pre O login e a senha não podem ser nulos ou vazios. O login não deve
     * existir previamente no sistema.
     * @post Um novo usuário é criado e persistido no sistema com os dados
     * fornecidos. O usuário retornado corresponde ao usuário recém-criado.
     */
    User registerNewUser(String login, String password, int avatarId);

    /**
     * Autentica um usuário com login e senha.
     *
     * @param login O login do usuário.
     * @param password A senha em texto puro para verificação.
     * @return Um Optional contendo o objeto User se a autenticação for
     * bem-sucedida, ou um Optional vazio caso contrário.
     * @throws IllegalArgumentException Se o login ou a senha forem nulos.
     * @pre O login e a senha não podem ser nulos.
     * @post Se as credenciais forem válidas para um usuário existente, o
     * Optional retornado contém o referido usuário. Caso contrário, o Optional
     * retornado está vazio.
     */
    Optional<User> authenticateUser(String login, String password);

    /**
     * Incrementa a pontuação de um usuário.
     *
     * @param userId O ID do usuário a ter a pontuação incrementada.
     * @return Nenhum retorno.
     * @throws IllegalArgumentException Se o ID do usuário for nulo ou não
     * corresponder a um usuário existente.
     * @pre O ID do usuário não pode ser nulo e deve corresponder a um usuário
     * existente no sistema.
     * @post A pontuação do usuário correspondente ao ID fornecido é
     * incrementada.
     */
    void incrementScore(Long userId);

    /**
     * Exclui um usuário, verificando se quem pede a exclusão tem permissão.
     *
     * @param userIdToDelete O ID do usuário a ser deletado.
     * @param requesterLogin O login do usuário que está fazendo a requisição.
     * @return Nenhum retorno.
     * @throws IllegalArgumentException Se o 'userIdToDelete' ou
     * 'requesterLogin' não corresponderem a usuários existentes.
     * @throws SecurityException Se o usuário solicitante não tiver permissão
     * para excluir o usuário alvo.
     * @pre O ID do usuário a ser deletado e o login do solicitante devem
     * corresponder a usuários existentes. O usuário solicitante deve ter
     * permissão para realizar a exclusão.
     * @post O usuário correspondente ao 'userIdToDelete' é permanentemente
     * removido do sistema.
     */
    void deleteUser(Long userIdToDelete, String requesterLogin);

    /**
     * Busca um usuário pelo seu login.
     *
     * @param login O login do usuário.
     * @return Um Optional contendo o objeto User se encontrado, ou um Optional
     * vazio caso contrário.
     * @throws IllegalArgumentException Se o login for nulo.
     * @pre O login não pode ser nulo.
     * @post Se um usuário com o login fornecido existir, o Optional retornado
     * contém o referido usuário. Caso contrário, o Optional está vazio.
     */
    Optional<User> findUserByLogin(String login);

    /**
     * Incrementa o número de simulações executadas por um usuário.
     *
     * @param userId O ID do usuário.
     * @return Nenhum retorno.
     * @throws IllegalArgumentException Se o ID do usuário for nulo ou não
     * corresponder a um usuário existente.
     * @pre O ID do usuário não pode ser nulo e deve corresponder a um usuário
     * existente no sistema.
     * @post O contador de simulações do usuário correspondente ao ID é
     * incrementado.
     */
    void incrementSimulationsRun(Long userId);
}
