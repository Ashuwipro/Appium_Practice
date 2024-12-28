package com.appium.appium.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Test1_CalculatorApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");

        //uncomment if the app is not already installed
        capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/android_apps/CalculatorApp.apk");
        capabilities.setCapability("appium:packageName", "com.swasin.CalculatorApp");
        capabilities.setCapability("appium:appWaitActivity", "com.swasin.CalculatorApp.MainActivity");
        capabilities.setCapability("appium:wdaLaunchTimeout", 120000); // Timeout in milliseconds

        //approach 1 for device specification
        capabilities.setCapability("appium:deviceName", "emulator-5554");
        capabilities.setCapability("appium:platformVersion", "12");

        capabilities.setCapability("appium:fastReset", true);

        // Initialize the driver
        AppiumDriver driver = null;

        try {
            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), capabilities);
            System.out.println("App launched successfully on iOS Simulator!");

            //clicking on button 8
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement btn8 = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("8")));
            btn8.click();

            //clicking on button +
            WebElement btnPlus = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"+\"]"));
            btnPlus.click();

            //clicking on button 2
            WebElement btn2 = driver.findElement(AppiumBy.accessibilityId("2"));
            btn2.click();

            //clicking on button =
            WebElement btnEqual = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"=\"]"));
            btnEqual.click();

            //verify the result
            WebElement result = driver.findElement(AppiumBy.xpath("//android.widget.TextView[2]"));
            String resultString = result.getText();

            if (resultString.equalsIgnoreCase("10")) {
                System.out.println("Pass");
            } else {
                System.out.println("Fail");
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the Appium server. Check the server URL.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while launching the app.");
        } finally {
            // Quit the driver after a delay to allow for testing or interaction
            if (driver != null) {
                try {
                    Thread.sleep(5000); // Keep the app open for 5 seconds (adjust as needed)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                driver.quit();
            }
        }
    }
}
