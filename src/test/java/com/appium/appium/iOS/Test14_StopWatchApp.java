package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.net.MalformedURLException;

public class Test14_StopWatchApp {

    public static void main(String[] args) {

        // Initialize the driver
        AppiumDriver driver = null;

        try {
            CommonMethods.startAppiumServer();
            driver = CommonMethods.setCapabilities("iPhone 16 Pro Max",
                    "18.1", "StopWatchApp");
            CommonMethods.takeScreenshot(driver, "app_launch");

            //validate that the initial time is 00:00:00
            WebElement timeValue = driver.findElement(AppiumBy.accessibilityId("timerText"));
            Assert.assertEquals(timeValue.getText(), "00:00:00");
            CommonMethods.takeScreenshot(driver, "initial_time");

            //click on start button
            driver.findElement(AppiumBy.accessibilityId("startButton")).click();

            //waiting for 10 seconds
            Thread.sleep(10000);

            //clicking on pause button
            driver.findElement(AppiumBy.accessibilityId("pauseButton")).click();
            CommonMethods.takeScreenshot(driver, "time_after_10sec");

            //validating that the time is showing 00:00:10
            Assert.assertEquals(timeValue.getText(), "00:00:10");

            //click on reset button
            driver.findElement(AppiumBy.accessibilityId("resetButton")).click();
            CommonMethods.takeScreenshot(driver, "time_reset");

            //validate that the time got reset to 00:00:00
            Assert.assertEquals(timeValue.getText(), "00:00:00");

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
