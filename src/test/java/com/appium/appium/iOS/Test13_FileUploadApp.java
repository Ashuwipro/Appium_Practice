package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.testng.Assert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class Test13_FileUploadApp {

    public static void main(String[] args) throws IOException {

        // Initialize the driver
        AppiumDriver driver = null;

        try {
            CommonMethods.startAppiumServer();
            driver = CommonMethods.setCapabilities("iPhone 16 Pro Max", "18.1", "FileUploadApp");
            CommonMethods.takeScreenshot(driver, "app_launch");
            String localFilePath = Paths.get(System.getProperty("user.dir")).toString()
                    .concat("/src/test/resources/");
            String localFieNameWithExt = "summary.pdf";
            CommonMethods.copyFileToSimulator("iPhone 16 Pro Max",
                    "18.1", CommonMethods.app_name,localFilePath, localFieNameWithExt);

            //validate that no file is currently selected
            String isFileSelected = driver.findElement(AppiumBy.accessibilityId("text_no_file_selected")).getText();
            Assert.assertEquals(isFileSelected, "No file selected");

            //clicking on Choose File button
            driver.findElement(AppiumBy.accessibilityId("button_choose_file")).click();

            //need to write code for selecting file in simulator
            CommonMethods.selectFileInSimulator(driver, localFieNameWithExt.substring(0, localFieNameWithExt.indexOf(".")));

            Thread.sleep(10000);
            //validating that the file got uploaded
            String fileName = driver.findElement(AppiumBy.accessibilityId("text_file_name")).getText();
            Assert.assertEquals(fileName, String.format("Selected File: %s", localFieNameWithExt));

            //click on Reset button
            driver.findElement(AppiumBy.accessibilityId("button_reset_file")).click();

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
