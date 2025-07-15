package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SimulationViewPage {

    private final  WebDriver driver;

    // CORRIGIDO: Localizador mais robusto. Procuramos por um elemento
    // que contenha o texto "Iteração", que sabemos que estará no InfoPanel.
    private final By infoPanelIndicator = By.xpath("//*[contains(text(), 'Iteração')]");

    public SimulationViewPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isSimulationViewVisible() {
        try {
            // A espera continua a mesma, mas agora com o localizador correto.
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(infoPanelIndicator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
