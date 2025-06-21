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
    private String password;
    private int avatarId;
    private int pontuation;
    private int simulationsRun;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.avatarId = 0;
        this.pontuation = 0;
        this.simulationsRun = 0;
    }

    public void incrementScore() {
        this.pontuation++;
    }

    public void incrementSimulationsRun() {
        this.simulationsRun++;
    }

    public void changeAvatar(int newAvatarId) {
        if (newAvatarId >= 0) {
            this.avatarId = newAvatarId;
        }
    }

    public double getAverageSuccessRate() {
        if (simulationsRun == 0) {
            return 0.0;
        }
        return (double) pontuation / simulationsRun;
    }
}
