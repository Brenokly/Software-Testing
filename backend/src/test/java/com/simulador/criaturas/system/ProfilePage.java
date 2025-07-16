package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfilePage {
  private final WebDriver driver;

  // Localizadores para a página e para o novo modal
  private final By deleteButton = By.cssSelector("[data-testid='delete-account-button']");
  private final By modalConfirmButton = By.cssSelector("[data-testid='confirm-delete-button']");
  private final By modal = By.cssSelector("[data-testid='confirmation-modal']");

  public ProfilePage(WebDriver driver) {
    this.driver = driver;
  }

  public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(deleteButton));
  }

  public void clickDeleteAccount() {
    driver.findElement(deleteButton).click();
  }

  // NOVO MÉTODO: Em vez de trocar para um alerta, ele interage com o modal
  public void confirmDeletionInModal() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    // 1. Espera o modal aparecer
    wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    // 2. Clica no botão de confirmação dentro do modal
    driver.findElement(modalConfirmButton).click();
  }
}