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

    public User(String login, String password, int avatarId) {
        this.login = login;
        this.password = password;
        this.avatarId = avatarId;
        this.pontuation = 0;
        this.simulationsRun = 0;
    }

    /**
     * Incrementa a pontuação do usuário.
     *
     * @param Nenhum parâmetro.
     * @return Nenhum retorno.
     * @pre O usuário deve existir.
     * @post A pontuação do usuário é incrementada em 1.
     * @throws Exception Nenhuma condição para exceção.
     */
    public void incrementScore() {
        this.pontuation++;
    }

    /**
     * Incrementa o número de simulações executadas pelo usuário.
     *
     * @param Nenhum parâmetro.
     * @return Nenhum retorno.
     * @pre O usuário deve existir.
     * @post O número de simulações executadas pelo usuário é incrementado em 1.
     * @throws Exception Nenhuma condição para exceção.
     */
    public void incrementSimulationsRun() {
        this.simulationsRun++;
    }

    /**
     * Altera o avatar do usuário.
     *
     * @param newAvatarId O novo ID do avatar.
     * @return Nenhum retorno.
     * @pre O ID do novo avatar deve ser maior ou igual a 0.
     * @post O avatar do usuário é alterado para o novo ID, se válido.
     * @throws Exception Nenhuma condição para exceção.
     */
    public void changeAvatar(int newAvatarId) {
        if (newAvatarId >= 0) {
            this.avatarId = newAvatarId;
        }
    }

    /**
     * Calcula a taxa média de sucesso do usuário.
     *
     * @param Nenhum parâmetro.
     * @return A taxa média de sucesso do usuário.
     * @pre O número de simulações executadas deve ser maior que 0.
     * @post A taxa média de sucesso é calculada como a pontuação dividida pelo
     * número de simulações executadas.
     * @throws Exception Nenhuma condição para exceção.
     */
    public double getAverageSuccessRate() {
        if (simulationsRun == 0) {
            return 0.0;
        }
        return (double) pontuation / simulationsRun;
    }
}
