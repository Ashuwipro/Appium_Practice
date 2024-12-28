package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

public class Test9_LongPress {

    // Initialize the driver
    public static AppiumDriver driver = null;

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/PhoneDialerApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.PhoneDialerApp");
        capabilities.setCapability("appium:wdaLaunchTimeout", 120000); // Timeout in milliseconds

        //approach 1 for device specification
        capabilities.setCapability("appium:deviceName", "iPhone 16 Pro Max");
        capabilities.setCapability("appium:platformVersion", "18.1");

        //approach 2 for device specification
        //capabilities.setCapability("appium:udid", "14B459CE-418A-46DC-89D0-75D6FF9B8282");

        try {
            CommonMethods.startAppiumServer();
            driver = new IOSDriver(new URL("http://127.0.0.1:4723/"), capabilities);
            System.out.println("App launched successfully on iOS Simulator!");

            //clicking on number buttons
            String[] Number = {"8", "9", "6", "0", "9", "4", "8", "6", "1", "6"};
            for (String num : Number) {
                driver.findElement(AppiumBy.accessibilityId("btn".concat(num))).click();
            }

            //verify the typed number
            String number = driver.findElement(AppiumBy.xpath("//XCUIElementTypeTextField[@name=\"phoneNumberDisplay\"]")).getText();
            if (number.equalsIgnoreCase(String.join("", Number))) {
                System.out.println("Pass");
            } else {
                System.out.println("Fail");
            }

            //doing long press
            WebElement clearButton = driver.findElement(AppiumBy.accessibilityId("clearButton"));
            longPress(clearButton);

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

    public static void longPress(WebElement element) {
        if (!element.isDisplayed()) {
            throw new IllegalStateException("Element is not visible!");
        }

        // Get the location of the element
        Point location = element.getLocation();
        System.out.println("Element location: " + location.x + ", " + location.y);

        // Define a touch input
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        // Move to the element's location and press
        sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.fromElement(element), 0,0));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

        // Pause for long press duration
        sequence.addAction(new Pause(finger, Duration.ofMillis(2000))); // Adjust duration as needed

        // Release the press
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        // Perform the sequence
        driver.perform(Collections.singletonList(sequence));
    }


}
