package com.simulador.criaturas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * A classe User representa um usuário do sistema.
 * Ela contém informações básicas como login, senha, ID do avatar e pontuação.
 * Um usuário poderá trocar seu avatar, visualizar suas estatísticas e participar de simulações.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private String login;
    private String password;
    private int avatarId;
    private int pontuation;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.avatarId = 0; // Default avatar ID
        this.pontuation = 0; // Default pontuation
    }
}
