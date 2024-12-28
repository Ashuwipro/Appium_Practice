package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

public class Test5_DragAndDropApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "iOS");
        capabilities.setCapability("appium:automationName", "XCUITest");

        //uncomment if the app is not already installed
        //capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/DragAndDropApp.app");
        capabilities.setCapability("appium:bundleId", "com.swasin.DragAndDropApp");
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

            //locating the sqaure to be dragged (left square)
            WebElement sourceElement = driver.findElement(AppiumBy.accessibilityId("left-square"));

            //locating the square to be dropped (right square)
            WebElement destinationElement = driver.findElement(AppiumBy.accessibilityId("right-square"));

            //perform drag and drop action - approach 1
//            Actions actions = new Actions(driver);
//            actions.clickAndHold(sourceElement)
//                    .moveToElement(destinationElement)
//                    .release()
//                    .perform();

            //perform drag and drop action - approach 2
            Point sourceElementCenter = CommonMethods.getCenter(sourceElement);
            Point destinationElementCenter = CommonMethods.getCenter(destinationElement);

            PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

            Sequence sequence = new Sequence(finger1, 1)
                    //move finger to the source element
                    .addAction(finger1.
                            createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                                    sourceElementCenter))
                    //finger coming down to contact the screen
                    .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    //pause for 500 millisecs
                    .addAction(new Pause(finger1, Duration.ofMillis(500)))
                    //move finger to the destination element
                    .addAction(finger1.
                            createPointerMove(Duration.ofMillis(500), PointerInput.Origin.viewport(),
                                    destinationElementCenter))
                    //move the finger up
                    .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

            //perform sequence of actions - approach 1
            driver.perform(Collections.singletonList(sequence));

            //perform sequence of actions - approach 2
            //driver.perform(Arrays.asList(sequence));

            //verify that the drag-and-drop was success
            WebElement dropText = driver.findElement(AppiumBy.className("XCUIElementTypeStaticText"));
            if (dropText.getText().equalsIgnoreCase("Fitted")) {
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
