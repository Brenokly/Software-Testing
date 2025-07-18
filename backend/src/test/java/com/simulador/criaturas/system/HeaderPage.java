package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HeaderPage {
  private final WebDriver driver;

  private final By profileLink = By.cssSelector("[data-testid='profile-link']");

  private final By rankingLink = By.linkText("Ranking");

  public HeaderPage(WebDriver driver) {
    this.driver = driver;
  }

  public void waitForHeaderLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(profileLink));
  }

  public void clickProfile() {
    driver.findElement(profileLink).click();
  }

  public void clickRanking() {
    this.waitForHeaderLoad();
    driver.findElement(rankingLink).click();
  }
}