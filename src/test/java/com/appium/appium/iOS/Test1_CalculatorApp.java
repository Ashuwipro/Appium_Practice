package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Test1_CalculatorApp {

    public static void main(String[] args) throws IOException {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/CalculatorApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.CalculatorApp");
        capabilities.setCapability("appium:wdaLaunchTimeout", 120000); // Timeout in milliseconds

        //approach 1 for device specification
        capabilities.setCapability("appium:deviceName", "iPhone 16 Pro Max");
        capabilities.setCapability("appium:platformVersion", "18.1");

        //approach 2 for device specification
        //capabilities.setCapability("appium:udid", "14B459CE-418A-46DC-89D0-75D6FF9B8282");

        // Initialize the driver
        AppiumDriver driver = null;
        CommonMethods.app_name = "CalculatorApp";

        try {
            CommonMethods.startAppiumServer();
            driver = new IOSDriver(new URL("http://127.0.0.1:4723/"), capabilities);
            System.out.println("App launched successfully on iOS Simulator!");
            CommonMethods.takeScreenshot(driver, "app_launch");

            //clicking on button 8
            WebElement btn8 = driver.findElement(AppiumBy.accessibilityId("8"));
            btn8.click();
            CommonMethods.takeScreenshot(driver, "btn_8_clicked");

            //clicking on button +
            WebElement btnPlus = driver.findElement(AppiumBy.xpath("//XCUIElementTypeOther[@name=\"+\"]"));
            btnPlus.click();
            CommonMethods.takeScreenshot(driver, "btn_+_clicked");

            //clicking on button 2
            WebElement btn2 = driver.findElement(AppiumBy.accessibilityId("2"));
            btn2.click();
            CommonMethods.takeScreenshot(driver, "btn_2_clicked");

            //clicking on button =
            WebElement btnEqual = driver.findElement(AppiumBy.xpath("//XCUIElementTypeOther[@name=\"=\"]"));
            btnEqual.click();
            CommonMethods.takeScreenshot(driver, "btn_=_clicked");

            //verify the result
            WebElement result = driver.findElement(AppiumBy.xpath("(//XCUIElementTypeStaticText)[2]"));
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
                CommonMethods.stopAppiumServer();
                CommonMethods.quitSimulator();
            }
        }
    }
}
