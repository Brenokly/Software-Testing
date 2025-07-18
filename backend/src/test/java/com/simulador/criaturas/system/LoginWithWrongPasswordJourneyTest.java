package com.simulador.criaturas.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginWithWrongPasswordJourneyTest extends SystemTestBase {

  @Test
  @DisplayName("Deve exibir erro ao tentar logar com senha incorreta")
  void shouldFailToLoginWithWrongPassword() {
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);

    // Registra um usuário válido ---
    String existingUser = "aventureiro_valido_" + System.currentTimeMillis();
    registerPage.navigateTo();
    registerPage.fillForm(existingUser, "senha_correta");
    registerPage.submit();
    loginPage.waitForPageLoad();

    // Tenta logar com a senha errada ---
    loginPage.fillForm(existingUser, "senha_incorreta_123");
    loginPage.submit();

    // Verificação ---
    assertThat(loginPage.isLoginErrorMessageVisible()).isTrue();
    assertThat(driver.getCurrentUrl()).contains("/login");
  }
}
