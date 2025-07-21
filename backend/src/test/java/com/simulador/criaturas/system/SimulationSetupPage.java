package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.config.TestConfig;

public class SimulationSetupPage {

    private final WebDriver driver;

    private final By creatureCountInput = By.cssSelector("input[type='number']");
    private final By startButton = By.xpath("//button[text()='Iniciar Batalha!']");

    public SimulationSetupPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
        wait.until(ExpectedConditions.visibilityOfElementLocated(creatureCountInput));
    }

    public void enterCreatureCount(String count) {
        driver.findElement(creatureCountInput).clear();
        driver.findElement(creatureCountInput).sendKeys(count);
    }

    public void clickStartBattle() {
        driver.findElement(startButton).click();
    }
}