package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.config.TestConfig;

public class LoginPage {

    private final WebDriver driver;
    private final String pageUrlFragment = "/login";

    private final By apiErrorLocator = By.xpath("//p[contains(text(), 'Login ou senha inválidos')]");
    private final By loginInput = By.name("login");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.xpath("//button[text()='Entrar na Guilda']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));

        wait.until(ExpectedConditions.urlContains(pageUrlFragment));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
    }

    public void fillForm(String login, String password) {
        driver.findElement(loginInput).sendKeys(login);
        driver.findElement(passwordInput).sendKeys(password);
    }

    public void submit() {
        driver.findElement(loginButton).click();
    }

    public boolean isLoginErrorMessageVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
            wait.until(ExpectedConditions.visibilityOfElementLocated(apiErrorLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}