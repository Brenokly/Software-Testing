package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.config.TestConfig;

public class ProfilePage {
  private final WebDriver driver;

  private final By deleteButton = By.cssSelector("[data-testid='delete-account-button']");
  private final By modalConfirmButton = By.cssSelector("[data-testid='confirm-delete-button']");
  private final By modal = By.cssSelector("[data-testid='confirmation-modal']");

  public ProfilePage(WebDriver driver) {
    this.driver = driver;
  }

  public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
    wait.until(ExpectedConditions.visibilityOfElementLocated(deleteButton));
  }

  public void clickDeleteAccount() {
    driver.findElement(deleteButton).click();
  }

  public void confirmDeletionInModal() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
    wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
    driver.findElement(modalConfirmButton).click();
  }
}