package com.simulador.criaturas.system.journey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SystemTestBase;

public class UserRegistrationAndLoginJourneyTest extends SystemTestBase {

    @Test
    @DisplayName("Jornada de Usuário: Deve registrar um novo usuário e fazer login com sucesso")
    void shouldSuccessfullyRegisterAndThenLogin() {
        RegisterPage registerPage = new RegisterPage(driver);
        LoginPage loginPage = new LoginPage(driver);

        String newUserLogin = "aventureiro_" + System.currentTimeMillis();
        String password = "senha123";

        registerPage.navigateTo();
        registerPage.fillForm(newUserLogin, password);
        registerPage.submit();

        assertTrue(registerPage.isSuccessMessageVisible(),
                "A mensagem de sucesso deveria aparecer na página de registro.");

        loginPage.waitForPageLoad();
        loginPage.fillForm(newUserLogin, password);
        loginPage.submit();

        WebDriverWait wait = new WebDriverWait(driver, explicitWaitTimeout);
        boolean onSimulationPage = wait.until(d -> d.getCurrentUrl().contains("/simulacao"));

        assertTrue(onSimulationPage, "O usuário deveria ser redirecionado para a página de simulação após o login.");
    }
}