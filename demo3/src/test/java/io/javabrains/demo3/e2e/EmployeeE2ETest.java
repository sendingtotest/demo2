package io.javabrains.demo3.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("e2e")
public class EmployeeE2ETest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    public static void teardownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void e2e_create_update_delete() throws InterruptedException {
        String base = "http://localhost:" + port + "/";
        driver.get(base);

        // wait briefly for page to load
        Thread.sleep(500);

        WebElement name = driver.findElement(By.id("employee-name"));
        WebElement dept = driver.findElement(By.id("employee-dept"));
        WebElement save = driver.findElement(By.id("save-btn"));

        name.sendKeys("Selenium User");
        dept.sendKeys("Automation");
        save.click();

        Thread.sleep(500);

        WebElement table = driver.findElement(By.cssSelector("#employees-table tbody"));
        assertThat(table.getText()).contains("Selenium User");

        // Click edit on the first row
        WebElement editBtn = driver.findElement(By.cssSelector("#employees-table tbody tr td button"));
        editBtn.click();
        Thread.sleep(300);

        WebElement nameField = driver.findElement(By.id("employee-name"));
        nameField.clear();
        nameField.sendKeys("Selenium User Updated");
        save.click();
        Thread.sleep(500);

        assertThat(table.getText()).contains("Selenium User Updated");

        // Delete
        WebElement deleteBtn = driver.findElement(By.cssSelector("#employees-table tbody tr td button.btn-outline-danger"));
        deleteBtn.click();
        // confirm alert
        driver.switchTo().alert().accept();
        Thread.sleep(500);

        assertThat(table.getText()).doesNotContain("Selenium User Updated");
    }
}
