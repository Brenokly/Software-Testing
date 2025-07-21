package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.config.TestConfig;

public class SimulationViewPage {

    private final WebDriver driver;

    private final By infoPanelIndicator = By.xpath("//*[contains(text(), 'Iteração')]");

    public SimulationViewPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isSimulationViewVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
            wait.until(ExpectedConditions.visibilityOfElementLocated(infoPanelIndicator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}