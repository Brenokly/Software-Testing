package com.simulador.criaturas.system;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserRegistrationAndLoginJourneyTest extends SystemTestBase {

    @Test
    @DisplayName("Jornada de Usuário: Deve registrar um novo usuário e fazer login com sucesso")
    void shouldSuccessfullyRegisterAndThenLogin() {
        // --- Objetos de Página ---
        RegisterPage registerPage = new RegisterPage(driver);
        LoginPage loginPage = new LoginPage(driver);

        String newUserLogin = "aventureiro_" + System.currentTimeMillis();
        String password = "senha123";

        // --- Parte 1: Jornada de Registro ---
        registerPage.navigateTo();
        registerPage.fillForm(newUserLogin, password);
        registerPage.submit();

        // --- Verificação Intermediária (na Página de Registro) ---
        // Agora verificamos a mensagem de sucesso na página correta (RegisterPage).
        assertTrue(registerPage.isSuccessMessageVisible(), "A mensagem de sucesso deveria aparecer na página de registro.");

        // --- Aguarda o redirecionamento e prossegue o Login ---
        // A lógica de espera agora fica na página de login, aguardando seus elementos.
        loginPage.waitForPageLoad();
        loginPage.fillForm(newUserLogin, password);
        loginPage.submit();

        // --- Verificação Final ---
        // Aguarda até que a URL mude para a página de simulação.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        boolean onSimulationPage = wait.until(d -> d.getCurrentUrl().contains("/simulacao"));

        assertTrue(onSimulationPage, "O usuário deveria ser redirecionado para a página de simulação após o login.");
    }
}
