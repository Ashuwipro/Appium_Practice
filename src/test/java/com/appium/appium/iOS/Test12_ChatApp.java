package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Test12_ChatApp {

    public static void main(String[] args) {

        int port1 = 4723;
        int port2 = 4724;

        Process nodeServerProcess = null;

        try {
            // Start the Node.js server
            nodeServerProcess = CommonMethods.startNodeServer("/Users/ashutoshpal/Desktop/react_native_project/ChatApp/server.js");

            // Stop Appium servers if already running
            CommonMethods.stopAppiumServer(port1);
            CommonMethods.stopAppiumServer(port2);

            // Start Appium servers
            CommonMethods.startAppiumServer(port1);
            CommonMethods.startAppiumServer(port2);

            // Wait to ensure servers are up
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to set up servers.");
            return;
        }

        // Desired Capabilities for Simulator 1
        DesiredCapabilities capabilities1 = new DesiredCapabilities();
        capabilities1.setCapability("appium:platformName", "iOS");
        capabilities1.setCapability("appium:automationName", "XCUITest");
        capabilities1.setCapability("appium:deviceName", "iPhone 16 Pro Max");
        capabilities1.setCapability("appium:platformVersion", "18.1");
        capabilities1.setCapability("appium:wdaLaunchTimeout", 120000);
        capabilities1.setCapability("appium:wdaLocalPort", 8100); // Unique WDA port for Simulator 1
        //uncomment if the app is not already installed
        capabilities1.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/ChatApp.app");
        capabilities1.setCapability("appium:bundleId", "com.swasin.ChatApp");
        //capabilities1.setCapability("fastReset", true); // This ensures app data is cleared

        // Desired Capabilities for Simulator 2
        DesiredCapabilities capabilities2 = new DesiredCapabilities();
        capabilities2.setCapability("appium:platformName", "iOS");
        capabilities2.setCapability("appium:automationName", "XCUITest");
        capabilities2.setCapability("appium:deviceName", "iPhone 15 Pro Max");
        capabilities2.setCapability("appium:platformVersion", "17.5");
        capabilities2.setCapability("appium:wdaLaunchTimeout", 120000);
        capabilities2.setCapability("appium:wdaLocalPort", 8101); // Unique WDA port for Simulator 2
        //uncomment if the app is not already installed
        capabilities2.setCapability("appium:app", "/Users/ashutoshpal/Desktop/iOS_apps/ChatApp.app");
        capabilities2.setCapability("appium:bundleId", "com.swasin.ChatApp");
        //capabilities2.setCapability("fastReset", true); // This ensures app data is cleared

        IOSDriver driver1 = null;
        IOSDriver driver2 = null;

        try {
            // Initialize Driver for Simulator 1
            System.out.println("Starting Appium session for Simulator 1...");
            driver1 = new IOSDriver(new URL("http://127.0.0.1:4723/"), capabilities1);
            System.out.println("App launched successfully on iOS Simulator 1");

            // Initialize Driver for Simulator 2
            System.out.println("Starting Appium session for Simulator 2...");
            driver2 = new IOSDriver(new URL("http://127.0.0.1:4724/"), capabilities2);
            System.out.println("App launched successfully on iOS Simulator 2");

            // Perform actions here
            Thread.sleep(10000);

            //device name xpath - do send keys
            //platform version xpath - do send keys
            //accessibility id connect button
            driver1.findElement(AppiumBy.xpath("//XCUIElementTypeTextField[@name=\"device-name-input\"]"))
                    .sendKeys("iPhone 15 Pro Max");
            driver1.findElement(AppiumBy.xpath("//XCUIElementTypeTextField[@name=\"platform-version-input\"]"))
                    .sendKeys("17.5");
            driver1.findElement(AppiumBy.accessibilityId("connect-button")).click();

            driver2.findElement(AppiumBy.xpath("//XCUIElementTypeTextField[@name=\"device-name-input\"]"))
                    .sendKeys("iPhone 16 Pro Max");
            driver2.findElement(AppiumBy.xpath("//XCUIElementTypeTextField[@name=\"platform-version-input\"]"))
                    .sendKeys("18.1");
            driver2.findElement(AppiumBy.accessibilityId("connect-button")).click();

            String[] messages = {"Hello IPhone 15 Pro Max", "Hi IPhone 16 Pro Max", "How are you?", "I'm good",
            "How are you?", "I'm good too"};
            int index=0;

            //message input field name - do send keys
            //message-input
            //accessibility id send button
            //send-button

            for(String message: messages) {

                if(index%2==0){
                    driver1.findElement(AppiumBy.accessibilityId("message-input")).sendKeys(message);
                    driver1.findElement(AppiumBy.accessibilityId("send-button")).click();
                }else{
                    driver2.findElement(AppiumBy.accessibilityId("message-input")).sendKeys(message);
                    driver2.findElement(AppiumBy.accessibilityId("send-button")).click();
                }

                //xpath for first text - remove [] and add index 0 to locate first message and so on
                // XCUIElementTypeStaticText[@name="Hello"]
                //validating that the message was sent and received properly
                Assert.assertEquals(message, driver1.findElement(AppiumBy.accessibilityId(message)).getText());
                Assert.assertEquals(message, driver2.findElement(AppiumBy.accessibilityId(message)).getText());

                index++;
                if(index==4){
                    index++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch the app on one or both simulators.");
        } finally {
            // Quit Driver 1
            if (driver1 != null) {
                try {
                    driver1.quit();
                    System.out.println("Driver 1 quit successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Quit Driver 2
            if (driver2 != null) {
                try {
                    driver2.quit();
                    System.out.println("Driver 2 quit successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Stop Appium servers
            try {
                CommonMethods.stopAppiumServer(port1);
                CommonMethods.stopAppiumServer(port2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Stop Node.js server
            if (nodeServerProcess != null) {
                nodeServerProcess.destroy();
                System.out.println("Node.js server stopped.");
            }
        }
        CommonMethods.quitSimulator();
    }
}
