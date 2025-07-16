package com.simulador.criaturas.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HeaderPage {
  private final WebDriver driver;

  private final By profileLink = By.cssSelector("[data-testid='profile-link']");

  public HeaderPage(WebDriver driver) {
    this.driver = driver;
  }

  public void clickProfile() {
    driver.findElement(profileLink).click();
  }
}