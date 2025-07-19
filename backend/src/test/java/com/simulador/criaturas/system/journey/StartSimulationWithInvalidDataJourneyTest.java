package com.simulador.criaturas.system.journey;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.SimulationSetupPage;
import com.simulador.criaturas.system.SystemTestBase;

// Teste de jornada do usuário para tentar iniciar uma simulação com dados inválidos
// e verificar se a mensagem de erro é exibida corretamente.

public class StartSimulationWithInvalidDataJourneyTest extends SystemTestBase {

  @Test
  @DisplayName("Deve exibir erro ao tentar iniciar simulação com dados inválidos")
  void shouldFailToStartSimulationWithInvalidData() {
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);
    SimulationSetupPage simulationSetupPage = new SimulationSetupPage(driver);

    // Registra e loga um usuário para chegar na tela de simulação ---
    String user = "aventureiro_simulador_" + System.currentTimeMillis();
    registerPage.navigateTo();
    registerPage.fillForm(user, "senha_correta");
    registerPage.submit();

    loginPage.waitForPageLoad();
    loginPage.fillForm(user, "senha_correta");
    loginPage.submit();

    simulationSetupPage.waitForPageLoad();

    // Insere dados inválidos (0 criaturas) ---
    simulationSetupPage.enterCreatureCount("0");
    simulationSetupPage.clickStartBattle();

    // --- Verificação ---
    By errorMessageLocator = By.xpath("//p[contains(text(), 'Por favor, insira um número entre 1 e 10.')]");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));

    assertThat(driver.findElement(errorMessageLocator).isDisplayed()).isTrue();
    assertThat(driver.getCurrentUrl()).contains("/simulacao");
  }
}
