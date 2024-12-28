package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;

public class Test4_ScrollAndSwitchApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/ScrollAndSwitchApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.ScrollAndSwitchApp");
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

            //clicking on Item 1 switch
            driver.findElement(AppiumBy.accessibilityId("switch-1")).click();

            //scrolling to Item 19
            HashMap<String, String> scrollObject = new HashMap<>();
            scrollObject.put("direction", "down");
            scrollObject.put("name", "itemName-19");
            driver.executeScript("mobile: scroll", scrollObject);

            //clicking on Item 19 switch
            driver.findElement(AppiumBy.xpath("switch-19")).click();

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
