package com.simulador.criaturas.system;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Esta classe de teste deve estender sua classe base que gerencia o WebDriver
public class StartSimulationJourneyTest extends SystemTestBase {

    @Test
    @DisplayName("Jornada de Usuário: Deve registrar, fazer login e iniciar uma simulação com sucesso")
    void shouldRegisterLoginAndStartSimulationSuccessfully() {
        // --- 1. Inicializa todos os Page Objects necessários para a jornada ---
        RegisterPage registerPage = new RegisterPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        SimulationSetupPage setupPage = new SimulationSetupPage(driver);
        SimulationViewPage viewPage = new SimulationViewPage(driver);

        // Gera um login único para cada execução do teste para evitar falhas por dados duplicados
        String newUserLogin = "aventureiro_sim_" + System.currentTimeMillis();
        String password = "senha_para_simulacao";

        // --- 2. Executa a Jornada ---
        // Parte A: Registro
        registerPage.navigateTo();
        registerPage.fillForm(newUserLogin, password);
        registerPage.submit();

        // Parte B: Login
        loginPage.waitForPageLoad();
        loginPage.fillForm(newUserLogin, password);
        loginPage.submit();

        // Parte C: Iniciar Simulação
        setupPage.waitForPageLoad();
        setupPage.enterCreatureCount("8");
        setupPage.clickStartBattle();

        // --- 3. Verificação Final ---
        // A propriedade final que queremos verificar é se, após todos os passos,
        // a tela da simulação foi carregada corretamente.
        assertTrue(viewPage.isSimulationViewVisible(), "A tela da simulação deveria estar visível após iniciar a batalha.");
    }
}
