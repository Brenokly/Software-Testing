package com.simulador.criaturas.system;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StatisticsPage {
  private final WebDriver driver;

  private final By rankingTitle = By.cssSelector("[data-testid='ranking-title']");
  private final By rankingRows = By.cssSelector("[data-testid='ranking-table-row']");
  private final By nextPageButton = By.cssSelector("[data-testid='next-page-button']");
  private final By pageIndicator = By.cssSelector("[data-testid='page-indicator']");

  public StatisticsPage(WebDriver driver) {
    this.driver = driver;
  }

  public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(rankingTitle));
    wait.until(ExpectedConditions.visibilityOfElementLocated(rankingRows));
  }

  public int getDisplayedUsersCount() {
    return driver.findElements(rankingRows).size();
  }

  public List<String> getDisplayedUserLogins() {
    List<WebElement> rows = driver.findElements(rankingRows);
    return rows.stream()
        .map(row -> row.findElement(By.xpath("./td[2]")).getText())
        .collect(Collectors.toList());
  }

  public void clickNextPage() {
    driver.findElement(nextPageButton).click();
  }

  public String getPageIndicatorText() {
    return driver.findElement(pageIndicator).getText();
  }

  public By getPageIndicatorLocator() {
    return pageIndicator;
  }
}