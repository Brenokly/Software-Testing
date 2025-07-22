package com.simulador.criaturas.system;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import com.simulador.criaturas.system.config.TestConfig;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public abstract class SystemTestBase {

    @Autowired
    private SpringDataUserRepository userRepository;

    protected static WebDriver driver;

    protected Duration explicitWaitTimeout;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        long timeoutSeconds = TestConfig.getTimeoutSeconds();
        System.out.println("INFO: Usando timeout explícito de: " + timeoutSeconds + " segundos.");
        this.explicitWaitTimeout = Duration.ofSeconds(timeoutSeconds);
    }

    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }
}