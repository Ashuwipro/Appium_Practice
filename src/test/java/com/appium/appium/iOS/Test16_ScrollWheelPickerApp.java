package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class Test16_ScrollWheelPickerApp {

    public static void main(String[] args) {

        AppiumDriver driver = null;
        String deviceName = "iPhone 16 Pro Max";
        String platformVersion = "18.1";
        String appName = "ScrollWheelPickerApp";

        try {
            CommonMethods.startAppiumServer();
            driver = CommonMethods.setCapabilities(deviceName,
                    platformVersion, appName);
            CommonMethods.clearAppScreenshotsDirectory();
            CommonMethods.takeScreenshot(driver, "app_launch");

            WebElement day = driver.findElement(AppiumBy.xpath("//XCUIElementTypePickerWheel[@name=\"day-picker\"]"));
            WebElement month = driver.findElement(AppiumBy.xpath("//XCUIElementTypePickerWheel[@name=\"month-picker\"]"));
            WebElement year = driver.findElement(AppiumBy.xpath("//XCUIElementTypePickerWheel[@name=\"year-picker\"]"));
            WebElement selectedDateText = driver.findElement(AppiumBy.accessibilityId("selected-date"));

            String selectedDate = day.getText().concat(" ").concat(month.getText())
                                    .concat(" ").concat(year.getText());
            System.out.println("SelectedDate1:="+selectedDate);
            System.out.println("SelectedDateText1:="+selectedDateText.getText());

            //validate date printed matches with the selected date
            Assert.assertEquals(selectedDateText.getText(), selectedDate);

            //scrolling to date 21
            CommonMethods.selectPickerValueBinarySearch(driver, day, "21");

            //scrolling to month jun
            CommonMethods.selectPickerValueLinearSearch(driver, month, "June");

            //scrolling to year 2024
            CommonMethods.selectPickerValueBinarySearch(driver, year, "2024");

            selectedDate = day.getText().concat(" ").concat(month.getText())
                    .concat(" ").concat(year.getText());
            System.out.println("SelectedDate2:="+selectedDate);
            System.out.println("SelectedDateText2:="+selectedDateText.getText());

            //validate updated date printed matches with the selected date
            Assert.assertEquals(selectedDateText.getText(), selectedDate);

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
