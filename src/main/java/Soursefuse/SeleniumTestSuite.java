package Soursefuse;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import utils.DatabaseUtils;
import utils.FieldTypeDetector;
import java.io.File;
import java.sql.*;
import java.util.List;

public class SeleniumTestSuite 
{
    WebDriver driver;

    @BeforeClass
    public void setup()
    {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @Test(priority = 1)
    public void testLoginWithRetry() 
    {
        int attempts = 0;
        boolean isLoggedIn = false;
        
        while (attempts < 3) 
        {
            try 
            {
                driver.get("https://sfwebhtml:t63KUfxL5vUyFLG4eqZNUcuRQ@dsnm875e4wmhw.cloudfront.net/");
                Thread.sleep(3000);
                isLoggedIn = driver.getCurrentUrl().contains("dashboard");
                if (isLoggedIn) break;
            }
            catch (Exception e)
            {
                attempts++;
            }
        }
        Assert.assertTrue(isLoggedIn, "Login failed after 3 attempts!");
    }

    @Test(priority = 2)
    public void testRequiredFieldErrors() 
    {
        driver.findElement(By.xpath("//button[text()='Submit']")).click();
        List<WebElement> errors = driver.findElements(By.cssSelector(".error-message"));

        for (WebElement error : errors) 
        {
            System.out.println("Field: " + error.getAttribute("data-field") + " -> Error: " + error.getText());
        }
    }

    @Test(priority = 3)
    public void testSoftAssertionsForFields() 
    {
        SoftAssert softAssert = new SoftAssert();
        
        softAssert.assertTrue(driver.findElement(By.id("email")).isDisplayed(), "Email field missing");
        softAssert.assertTrue(driver.findElement(By.id("password")).isDisplayed(), "Password field missing");
        
        softAssert.assertAll();
    }

    @Test(priority = 4)
    public void testFormSubmission() 
    {
        driver.findElement(By.xpath("//input[@id='dob']")).sendKeys("05/11/1990");
        driver.findElement(By.xpath("//input[@id='fileUpload']")).sendKeys(new File("sample.pdf").getAbsolutePath());
        driver.findElement(By.xpath("//input[@value='Yes']")).click();
        driver.findElement(By.xpath("//button[text()='Submit']")).click();
    }

    @Test(priority = 5)
    public void testFieldTypeDetection() 
    {
        WebElement nameField = driver.findElement(By.id("name"));
        WebElement genderField = driver.findElement(By.name("gender"));
        WebElement countryField = driver.findElement(By.name("country"));

        System.out.println("Field: Name -> Type: " + FieldTypeDetector.detectFieldType(nameField));
        System.out.println("Field: Gender -> Type: " + FieldTypeDetector.detectFieldType(genderField));
        System.out.println("Field: Country -> Type: " + FieldTypeDetector.detectFieldType(countryField));
    }

    @Test(priority = 6)
    public void testDatabaseEntry()
    {
        boolean entryExists = DatabaseUtils.isRecordPresent("test@example.com");
        Assert.assertTrue(entryExists, "Entry was not created in DB");
    }

    @Test(priority = 7)
    public void testEmailTrigger()
    {
        boolean emailSent = DatabaseUtils.isEmailSent("test@example.com");
        Assert.assertTrue(emailSent, "Email was not triggered!");
    }

    @AfterClass
    public void teardown()
    {
        driver.quit();
    }
}
