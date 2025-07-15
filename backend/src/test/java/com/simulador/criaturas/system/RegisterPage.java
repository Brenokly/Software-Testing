package com.simulador.criaturas.system;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterPage {

    private final WebDriver driver;
    private final String pageUrl = "http://localhost:3000/register";

    private final By loginInput = By.name("login");
    private final By passwordInput = By.name("password");
    private final By confirmPasswordInput = By.name("confirmPassword");
    private final By registerButton = By.xpath("//button[text()='Juntar-se à Guilda']");

    private final By successMessage = By.xpath("//p[contains(text(), 'Conta criada com sucesso!')]");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateTo() {
        driver.get(pageUrl);
    }

    public void fillForm(String login, String password) {
        // Preenche os campos de login, senha e confirmação de senha
        driver.findElement(loginInput).sendKeys(login);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(confirmPasswordInput).sendKeys(password);
    }

    public void submit() {
        // Clica no botão de registro
        driver.findElement(registerButton).click();
    }

    public boolean isSuccessMessageVisible() {
        // Espera até que a mensagem de sucesso esteja visível
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
