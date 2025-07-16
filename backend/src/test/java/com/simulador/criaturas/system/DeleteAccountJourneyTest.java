package com.simulador.criaturas.system;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(d -> d.getCurrentUrl().contains("/simulacao"));

    headerPage.clickProfile();

    profilePage.waitForPageLoad();
    profilePage.clickDeleteAccount();

    profilePage.confirmDeletionInModal();

    boolean onHomePage = wait.until(d -> d.getCurrentUrl().endsWith("/"));
    assertTrue(onHomePage, "O usuário deveria ser redirecionado para a home após deletar a conta.");

    driver.get("http://localhost:3000/login");
    loginPage.waitForPageLoad();
    loginPage.fillForm(userToDelete, password);
    loginPage.submit();

    assertTrue(loginPage.isLoginErrorMessageVisible(),
        "Deveria aparecer uma mensagem de erro ao tentar logar com a conta excluída.");
  }
}