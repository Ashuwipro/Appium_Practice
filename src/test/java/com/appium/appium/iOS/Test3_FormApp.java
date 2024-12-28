package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class Test3_FormApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/FormApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.FormApp");
        capabilities.setCapability("appium:wdaLaunchTimeout", 120000); // Timeout in milliseconds

        //approach 1 for device specification
        capabilities.setCapability("appium:deviceName", "iPhone 16 Pro Max");
        capabilities.setCapability("appium:platformVersion", "18.1");

        //approach 2 for device specification
        //capabilities.setCapability("appium:udid", "14B459CE-418A-46DC-89D0-75D6FF9B8282");

        // Initialize the driver
        AppiumDriver driver = null;

        try {
            CommonMethods.startAppiumServer();
            driver = new IOSDriver(new URL("http://127.0.0.1:4723/"), capabilities);
            System.out.println("App launched successfully on iOS Simulator!");

            //enter text in textbox
            WebElement textbox = driver.findElement(AppiumBy.className("XCUIElementTypeTextField"));
            textbox.sendKeys("Ashutosh Pal");

            //selecting radio button
            driver.findElement(AppiumBy.accessibilityId("radioOption1")).click();

            //selecting checkbox
            driver.findElement(AppiumBy.accessibilityId("checkbox1")).click();

            //click on submit button
            driver.findElement(AppiumBy.accessibilityId("submitButton")).click();

            //verify the values entered
            WebElement enteredText = driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[contains(@name, \"Entered Text\")]"));
            WebElement radioSelected = driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[contains(@name, \"Selected Radio\")]"));
            WebElement checkSelected = driver.findElement(AppiumBy.xpath("//XCUIElementTypeStaticText[contains(@name, \"Selected Checkboxes\")]"));
            Assert.assertTrue(enteredText.getText().contains("Ashutosh Pal"));
            Assert.assertTrue(radioSelected.getText().contains("Option 1"));
            Assert.assertTrue(checkSelected.getText().contains("checkbox1"));

            //click on close button
            driver.findElement(AppiumBy.accessibilityId("Close")).click();

            //click on reset button
            driver.findElement(AppiumBy.accessibilityId("resetButton")).click();

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
                CommonMethods.stopAppiumServer();
                CommonMethods.quitSimulator();
            }
        }
    }
}
