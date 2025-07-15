package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SimulationViewPage {

    private WebDriver driver;

    // O botão "Retornar" é um bom indicador de que a simulação começou
    private By returnButton = By.xpath("//button[text()='Retornar']");

    public SimulationViewPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isSimulationViewVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(returnButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
