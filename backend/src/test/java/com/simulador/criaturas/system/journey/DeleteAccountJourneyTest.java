package com.simulador.criaturas.system.journey;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.HeaderPage;
import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.ProfilePage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SystemTestBase;
import com.simulador.criaturas.system.config.TestConfig;

// Teste de jornada do usuário para deletar a própria conta
// e verificar se o usuário é deletado corretamente.

public class DeleteAccountJourneyTest extends SystemTestBase {

  @Test
  @DisplayName("Jornada de Usuário: Deve deletar a própria conta e ser deslogado")
  void shouldDeleteOwnAccountAndBeLoggedOut() {
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);
    HeaderPage headerPage = new HeaderPage(driver);
    ProfilePage profilePage = new ProfilePage(driver);

    String userToDelete = "usuario_a_ser_deletado_" + System.currentTimeMillis();
    String password = "password";

    registerPage.navigateTo();
    registerPage.fillForm(userToDelete, password);
    registerPage.submit();

    loginPage.waitForPageLoad();
    loginPage.fillForm(userToDelete, password);
    loginPage.submit();

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
    wait.until(d -> d.getCurrentUrl().contains("/simulacao"));

    headerPage.clickProfile();

    profilePage.waitForPageLoad();
    profilePage.clickDeleteAccount();

    profilePage.confirmDeletionInModal();

    boolean onHomePage = wait.until(d -> d.getCurrentUrl().equals(TestConfig.getBaseUrl() + "/"));
    assertTrue(onHomePage, "O usuário deveria ser redirecionado para a home após deletar a conta.");

    driver.get(TestConfig.getBaseUrl() + "/login");
    loginPage.waitForPageLoad();
    loginPage.fillForm(userToDelete, password);
    loginPage.submit();

    assertTrue(loginPage.isLoginErrorMessageVisible(),
        "Deveria aparecer uma mensagem de erro ao tentar logar com a conta excluída.");
  }
}