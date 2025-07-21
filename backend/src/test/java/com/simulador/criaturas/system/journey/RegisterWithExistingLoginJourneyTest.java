package com.simulador.criaturas.system.journey;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SystemTestBase;

public class RegisterWithExistingLoginJourneyTest extends SystemTestBase {

  @Test
  @DisplayName("Deve exibir erro ao tentar registrar com um login que já existe")
  void shouldFailToRegisterWithExistingLogin() {
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);

    // --- Setup: Registra um usuário para que o login já exista ---
    String existingUser = "aventureiro_existente_" + System.currentTimeMillis();
    registerPage.navigateTo();
    registerPage.fillForm(existingUser, "senha_correta");
    registerPage.submit();
    loginPage.waitForPageLoad();

    // Tenta registrar novamente
    registerPage.navigateTo();
    registerPage.fillForm(existingUser, "qualquer_senha");
    registerPage.submit();

    // --- Verificação ---
    By errorMessageLocator = By.xpath("//p[contains(text(), 'Erro: Login já está em uso.')]");

    WebDriverWait wait = new WebDriverWait(driver, explicitWaitTimeout);
    wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));

    assertThat(driver.findElement(errorMessageLocator).isDisplayed()).isTrue();
    assertThat(driver.getCurrentUrl()).contains("/register");
  }
}