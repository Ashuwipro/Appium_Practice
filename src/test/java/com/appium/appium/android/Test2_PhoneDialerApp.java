package com.appium.appium.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class Test2_PhoneDialerApp {

    public static void main(String[] args) {
        // Set the Desired Capabilities
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("appium:platformName", "android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");

        //uncomment if the app is not already installed
        capabilities.setCapability("appium:app", "/Users/ashutoshpal/Desktop/android_apps/PhoneDialerApp.apk");
        capabilities.setCapability("appium:packageName", "com.swasin.PhoneDialerApp");
        capabilities.setCapability("appium:appWaitActivity", "com.swasin.PhoneDialerApp.MainActivity");
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

            Thread.sleep(3000);

            //clicking on number buttons
            String[] Number = {"8", "9", "6", "0", "9", "4", "8", "6", "1", "6"};
            for(String num: Number){
                driver.findElement(AppiumBy.accessibilityId("btn".concat(num))).click();
            }

            //verify the typed number
            String number = driver.findElement(AppiumBy.className("android.widget.EditText")).getText();
            if(number.equalsIgnoreCase(String.join("", Number))){
                System.out.println("Pass");
            }else{
                System.out.println("Fail");
            }

            //clicking on call button
            driver.findElement(AppiumBy.accessibilityId("callButton")).click();

            //cut the call
            driver.findElement(AppiumBy.accessibilityId("cutCallButton")).click();

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
