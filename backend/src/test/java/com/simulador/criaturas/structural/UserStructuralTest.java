package com.simulador.criaturas.structural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.domain.model.User;

public class UserStructuralTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testuser", "password", 5, 10, 20);
    }

    @Test
    @DisplayName("Cobre os métodos de lógica linear (construtores e incrementadores)")
    void shouldExecuteLinearLogicMethods() {
        User user1 = new User();
        user1.setLogin("login");
        user1.setPassword("pass");
        user1.setAvatarId(1);
        assertNotNull(user1);

        User user2 = new User();
        user2.setLogin("login2");
        user2.setPassword("pass2");
        user2.setAvatarId(2);
        assertNotNull(user2);

        int initialScore = user.getPontuation();
        user.incrementScore();
        assertEquals(initialScore + 1, user.getPontuation());

        int initialRuns = user.getSimulationsRun();
        user.incrementSimulationsRun();
        assertEquals(initialRuns + 1, user.getSimulationsRun());
    }

    @Test
    @DisplayName("changeAvatar: Cobre o caminho IF (condição verdadeira)")
    void changeAvatar_shouldFollowTruePath_whenIdIsValid() {
        user.changeAvatar(10);
        assertEquals(10, user.getAvatarId());
    }

    @Test
    void changeAvatar_shouldFollowFalsePath_whenIdIsInvalid() {
        int initialAvatarId = user.getAvatarId();
        user.changeAvatar(-1);
        assertEquals(initialAvatarId, user.getAvatarId());
    }

    @Test
    void getAverageSuccessRate_shouldFollowTruePath_whenSimulationsRunIsZero() {
        user.setSimulationsRun(0);
        double rate = user.getAverageSuccessRate();
        assertEquals(0.0, rate);
    }

    @Test
    void getAverageSuccessRate_shouldFollowElsePath_whenSimulationsRunIsNotZero() {
        double rate = user.getAverageSuccessRate();
        assertEquals(0.5, rate);
    }
}
