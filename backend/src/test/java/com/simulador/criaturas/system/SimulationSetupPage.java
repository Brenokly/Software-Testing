package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SimulationSetupPage {

    private WebDriver driver;

    // Localizadores baseados no seu código React
    private By creatureCountInput = By.cssSelector("input[type='number']");
    private By startButton = By.xpath("//button[text()='Iniciar Batalha!']");

    public SimulationSetupPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Espera até que o input para o número de criaturas esteja visível
        wait.until(ExpectedConditions.visibilityOfElementLocated(creatureCountInput));
    }

    public void enterCreatureCount(String count) {
        driver.findElement(creatureCountInput).clear(); // Limpa o valor padrão
        driver.findElement(creatureCountInput).sendKeys(count);
    }

    public void clickStartBattle() {
        driver.findElement(startButton).click();
    }
}
