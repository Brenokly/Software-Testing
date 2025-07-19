package com.simulador.criaturas.system.journey;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SystemTestBase;

// Teste de jornada do usuário para tentar logar com senha incorreta
// e verificar se a mensagem de erro é exibida corretamente.

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
