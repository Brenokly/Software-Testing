package com.simulador.criaturas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa o Usuário no nosso DOMÍNIO DE NEGÓCIO. Esta classe é pura, sem
 * anotações de frameworks. Ela contém os dados e as regras de negócio do
 * usuário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String login;
    private String password; // Em um sistema real, este campo guardaria a senha criptografada
    private int avatarId;
    private int pontuation;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.avatarId = 0;
        this.pontuation = 0;
    }

    // Exemplo de regra de negócio que vive no domínio:
    public void incrementScore() {
        this.pontuation++;
    }

    public void changeAvatar(int newAvatarId) {
        if (newAvatarId >= 0) {
            this.avatarId = newAvatarId;
        }
    }
}
