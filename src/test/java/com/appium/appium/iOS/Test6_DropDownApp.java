package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class Test6_DropDownApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/DropDownApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.DropDownApp");
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

            //clicking on dropdown
            driver.findElement(AppiumBy.accessibilityId("dropdownArrow")).click();

            //scrolling to option 26
            HashMap<String, String> scrollObject = new HashMap<>();
            scrollObject.put("direction", "down");
            scrollObject.put("accessibilityId", "dropdownOption-25"); //0 based used in UI
            driver.executeScript("mobile: scroll", scrollObject);

            //clicking on option 26
            driver.findElement(AppiumBy.accessibilityId("dropdownOption-25")).click();

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
