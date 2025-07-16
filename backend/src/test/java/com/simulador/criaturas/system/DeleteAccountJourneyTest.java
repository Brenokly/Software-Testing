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
    // --- Page Objects ---
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);
    HeaderPage headerPage = new HeaderPage(driver);
    ProfilePage profilePage = new ProfilePage(driver);

    String userToDelete = "usuario_a_ser_deletado_" + System.currentTimeMillis();
    String password = "password";

    // --- Parte 1: Setup - Registrar e Logar um usuário ---
    registerPage.navigateTo();
    registerPage.fillForm(userToDelete, password);
    registerPage.submit();

    loginPage.waitForPageLoad();
    loginPage.fillForm(userToDelete, password);
    loginPage.submit();

    // --- Verificação Intermediária: Confirma que o login foi bem-sucedido ---
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(d -> d.getCurrentUrl().contains("/simulacao"));

    // --- Parte 2: Jornada de Exclusão ---
    headerPage.clickProfile();

    profilePage.waitForPageLoad();
    profilePage.clickDeleteAccount();

    // O Selenium precisa de uma instrução para interagir com o alerta do navegador
    profilePage.confirmDeletionInModal();

    // --- Verificação Final ---
    // Após deletar, o usuário deve ser redirecionado para a página inicial ou de
    // login.
    // Vamos verificar se ele foi para a página inicial, que não tem "/login" na
    // URL.
    boolean onHomePage = wait.until(d -> d.getCurrentUrl().endsWith("/"));
    assertTrue(onHomePage, "O usuário deveria ser redirecionado para a home após deletar a conta.");

    // Para ter certeza, tentamos logar de novo e esperamos que falhe.
    driver.get("http://localhost:3000/login");
    loginPage.waitForPageLoad();
    loginPage.fillForm(userToDelete, password);
    loginPage.submit();

    // Verificamos se uma mensagem de erro de login aparece (pois o usuário não
    // existe mais)
    assertTrue(loginPage.isLoginErrorMessageVisible(),
        "Deveria aparecer uma mensagem de erro ao tentar logar com a conta excluída.");
  }
}