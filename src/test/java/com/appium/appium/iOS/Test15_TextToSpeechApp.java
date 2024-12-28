package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.time.Duration;

public class Test15_TextToSpeechApp {

    public static void main(String[] args) {

        // Initialize the driver
        AppiumDriver driver = null;
        String deviceName = "iPhone 16 Pro Max";
        String platformVersion = "18.1";
        String appName = "TextToSpeechApp";

        try {
            CommonMethods.startAppiumServer();
            driver = CommonMethods.setCapabilities(deviceName,
                    platformVersion, appName);
            CommonMethods.clearAppScreenshotsDirectory();
            CommonMethods.takeScreenshot(driver, "app_launch");

            //entering text
            WebElement inputBox = driver.findElement(AppiumBy.accessibilityId("inputText"));
            inputBox.sendKeys("I am a boy who is currently learning appium on youtube. " +
                    "Also I have prepared many test cases for practicing appium. For that I created many apps and then " +
                    "I tested those app by writting the appium code in java.");
            CommonMethods.takeScreenshot(driver, "text_entered");

            //selecting female voice
            WebElement voiceSelect = driver.findElement(AppiumBy.accessibilityId("voiceDropdown"));
            voiceSelect.click();
            driver.findElement(AppiumBy.accessibilityId("voiceOption_Female")).click();
            CommonMethods.takeScreenshot(driver, "voice_female_selected");

            //click on Play Audio button
            WebElement playAudioBtn = driver.findElement(AppiumBy.accessibilityId("speakButton"));
            playAudioBtn.click();
            CommonMethods.takeScreenshot(driver, "play_audio_female_clicked");

            //wait until the Play Audio button gets enabled
            boolean isEnabled = false;
            while(!isEnabled){
                isEnabled = playAudioBtn.isEnabled();
            }

            //selecting male voice
            voiceSelect.click();
            driver.findElement(AppiumBy.accessibilityId("voiceOption_Male")).click();
            CommonMethods.takeScreenshot(driver, "voice_male_selected");

            playAudioBtn.click();
            CommonMethods.takeScreenshot(driver, "play_audio_male_clicked");

            isEnabled= false;
            while(!isEnabled){
                isEnabled = playAudioBtn.isEnabled();
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
