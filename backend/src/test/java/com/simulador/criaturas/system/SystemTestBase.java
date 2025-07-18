package com.simulador.criaturas.system;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.simulador.criaturas.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SystemTestBase {

    @Autowired
    private SpringDataUserRepository userRepository;

    protected static WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }
}