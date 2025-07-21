package com.simulador.criaturas.system.journey;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.HeaderPage;
import com.simulador.criaturas.system.LoginPage;
import com.simulador.criaturas.system.RegisterPage;
import com.simulador.criaturas.system.StatisticsPage;
import com.simulador.criaturas.system.SystemTestBase;

// Teste de jornada do usuário para visualizar estatísticas
// e verificar se a paginação e os dados estão corretos.

public class ViewStatisticsJourneyTest extends SystemTestBase {

  private void registerUser(String username, String password) {
    RegisterPage registerPage = new RegisterPage(driver);
    LoginPage loginPage = new LoginPage(driver);

    registerPage.navigateTo();

    registerPage.fillForm(username, password);
    registerPage.submit();

    loginPage.waitForPageLoad();
  }

  @BeforeEach
  void setupDatabase() {

    registerUser("a_aventureiro", "senha_para_simulacao");
    registerUser("b_aventureiro", "senha_para_simulacao");
    registerUser("c_aventureiro", "senha_para_simulacao");
    registerUser("d_aventureiro", "senha_para_simulacao");
    registerUser("e_aventureiro", "senha_para_simulacao");
    registerUser("f_aventureiro", "senha_para_simulacao");
    registerUser("g_aventureiro", "senha_para_simulacao");
  }

  @Test
  @DisplayName("Jornada de Usuário: Deve navegar para o ranking e ver a primeira página e a paginação")
  void shouldNavigateToRankingAndPaginate() {
    HeaderPage headerPage = new HeaderPage(driver);
    StatisticsPage statisticsPage = new StatisticsPage(driver);
    LoginPage loginPage = new LoginPage(driver);

    loginPage.waitForPageLoad();
    loginPage.fillForm("g_aventureiro", "senha_para_simulacao");
    loginPage.submit();

    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(d -> d.getCurrentUrl().contains("/simulacao"));

    headerPage.clickRanking();
    statisticsPage.waitForPageLoad();

    assertThat(statisticsPage.getDisplayedUsersCount()).isEqualTo(5);
    assertThat(statisticsPage.getPageIndicatorText()).isEqualTo("Página 1 de 3");

    // --- PAGINAÇÃO PARA PÁGINA 2 ---
    statisticsPage.clickNextPage();

    // ESPERA ROBUSTA: espera até que o elemento correto tenha o texto esperado.
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.textToBe(statisticsPage.getPageIndicatorLocator(), "Página 2 de 3"));

    assertThat(statisticsPage.getDisplayedUsersCount()).isEqualTo(5);
    assertThat(statisticsPage.getPageIndicatorText()).isEqualTo("Página 2 de 3"); // A asserção continua importante
    assertThat(statisticsPage.getDisplayedUserLogins()).containsExactly(
        "a_aventureiro",
        "b_aventureiro",
        "c_aventureiro",
        "d_aventureiro",
        "e_aventureiro");

    // --- PAGINAÇÃO PARA PÁGINA 3 ---
    statisticsPage.clickNextPage();

    // ESPERA ROBUSTA novamente
    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.textToBe(statisticsPage.getPageIndicatorLocator(), "Página 3 de 3"));

    assertThat(statisticsPage.getDisplayedUsersCount()).isEqualTo(2);
    assertThat(statisticsPage.getPageIndicatorText()).isEqualTo("Página 3 de 3");
    assertThat(statisticsPage.getDisplayedUserLogins()).containsExactly("f_aventureiro", "g_aventureiro");
  }

}