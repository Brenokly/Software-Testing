package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.simulador.criaturas.system.config.TestConfig;

public class RegisterPage {

    private final WebDriver driver;

    private final By loginInput = By.name("login");
    private final By passwordInput = By.name("password");
    private final By confirmPasswordInput = By.name("confirmPassword");
    private final By registerButton = By.xpath("//button[text()='Juntar-se à Guilda']");
    private final By successMessage = By.xpath("//p[contains(text(), 'Conta criada com sucesso!')]");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateTo() {
        driver.get(TestConfig.getBaseUrl() + "/register");
    }

    public void fillForm(String login, String password) {
        driver.findElement(loginInput).sendKeys(login);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(confirmPasswordInput).sendKeys(password);
    }

    public void submit() {
        driver.findElement(registerButton).click();
    }

    public boolean isSuccessMessageVisible() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.getTimeoutSeconds()));
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}