package com.simulador.criaturas.system.journey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SimulationSetupPage;
import com.simulador.criaturas.system.SimulationViewPage;
import com.simulador.criaturas.system.SystemTestBase;

// Teste de jornada do usuário para registrar, fazer login e iniciar uma simulação
// e verificar se a simulação é iniciada corretamente.

public class StartSimulationJourneyTest extends SystemTestBase {

    @Test
    @DisplayName("Jornada de Usuário: Deve registrar, fazer login e iniciar uma simulação com sucesso")
    void shouldRegisterLoginAndStartSimulationSuccessfully() {
        RegisterPage registerPage = new RegisterPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        SimulationSetupPage setupPage = new SimulationSetupPage(driver);
        SimulationViewPage viewPage = new SimulationViewPage(driver);

        String newUserLogin = "aventureiro_sim_" + System.currentTimeMillis();
        String password = "senha_para_simulacao";

        registerPage.navigateTo();
        registerPage.fillForm(newUserLogin, password);
        registerPage.submit();

        loginPage.waitForPageLoad();
        loginPage.fillForm(newUserLogin, password);
        loginPage.submit();

        setupPage.waitForPageLoad();
        setupPage.enterCreatureCount("8");
        setupPage.clickStartBattle();

        assertTrue(viewPage.isSimulationViewVisible(),
                "A tela da simulação deveria estar visível após iniciar a batalha.");
    }
}
