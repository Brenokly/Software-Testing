package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final String pageUrlFragment = "/login";

    private final By loginInput = By.name("login");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.xpath("//button[text()='Entrar na Guilda']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Espera até que a URL contenha o fragmento da página de login
        wait.until(ExpectedConditions.urlContains(pageUrlFragment));

        // Espera até que o campo de login esteja visível e clicável
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
    }

    public void fillForm(String login, String password) {
        // Preenche os campos de login e senha
        driver.findElement(loginInput).sendKeys(login);
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void submit() {
        // Clica no botão de login
        driver.findElement(loginButton).click();
    }
}
