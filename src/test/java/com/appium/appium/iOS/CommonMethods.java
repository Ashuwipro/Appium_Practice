package com.appium.appium.iOS;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommonMethods {

    public static String app_name;
    private static final String APPIUM_COMMAND = "appium";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4723;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 30000;

    public static void quitSimulator() {
        try {
            // Terminal command to quit the Simulator
            String[] cmd = {"killall", "Simulator"};

            // Execute the command
            Process process = Runtime.getRuntime().exec(cmd);

            // Wait for the process to complete
            process.waitFor();

            // Check if it completed successfully
            if (process.exitValue() == 0) {
                System.out.println("Simulator quit successfully.");
            } else {
                System.err.println("Error while quitting the Simulator.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void takeScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Take screenshot and store it as a file
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Set the destination path for the screenshot
            String projectRoot = Paths.get(System.getProperty("user.dir")).toString();
            String destinationPath = projectRoot + String.format("/screenshots/%s/", app_name) + screenshotName + ".png";

            // Create the screenshots directory if it doesn't exist
            File destFile = new File(destinationPath);
            destFile.getParentFile().mkdirs();

            // Copy the screenshot to the destination
            FileUtils.copyFile(srcFile, destFile);

            System.out.println("Screenshot saved at: " + destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    public static boolean isAppiumServerRunning() {
        try (Socket socket = new Socket(HOST, PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void startAppiumServer() throws IOException, InterruptedException {
        int attempt = 1;

        // Retry loop
        while (attempt <= MAX_RETRIES) {
            if (isAppiumServerRunning()) {
                System.out.println("Appium server is already running.");
                return;
            }

            System.out.println("Attempt " + attempt + " to start Appium server...");

            // Try starting the Appium server
            ProcessBuilder processBuilder = new ProcessBuilder(
                    APPIUM_COMMAND,
                    "--address", HOST,
                    "--port", String.valueOf(PORT),
                    "--log-level", "error"
            );

            processBuilder.redirectError(ProcessBuilder.Redirect.to(new File("/dev/null")));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(new File("/dev/null")));

            Process process = processBuilder.start();

            // Wait for a few seconds for the server to start
            Thread.sleep(5000);

            if (isAppiumServerRunning()) {
                System.out.println("Appium server started successfully.");
                return; // Server started successfully, exit method
            } else {
                System.err.println("Failed to start Appium server.");
                if (attempt < MAX_RETRIES) {
                    System.out.println("Retrying in " + RETRY_DELAY / 1000 + " seconds...");
                    Thread.sleep(RETRY_DELAY);  // Wait before retrying
                }
            }
            attempt++;
        }

        // If all attempts fail
        System.err.println("Failed to start Appium server after " + MAX_RETRIES + " attempts.");
    }


    public static void stopAppiumServer() {
        try {
            System.out.println("Stopping Appium server...");

            // Command to find the process running on the Appium port (4723)
            String[] findCommand = {
                    "/bin/sh",
                    "-c",
                    "lsof -i :4723 | grep LISTEN | awk '{print $2}'"
            };

            // Execute the command to find the PID
            Process findProcess = Runtime.getRuntime().exec(findCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(findProcess.getInputStream()));
            String pid = reader.readLine();
            reader.close(); // Close the reader to prevent "Stream closed" errors

            if (pid != null && !pid.isEmpty()) {
                // Kill the process using the PID
                String[] killCommand = {
                        "kill", "-9", pid
                };
                Process killProcess = Runtime.getRuntime().exec(killCommand);
                killProcess.waitFor(); // Wait for the kill command to complete
                System.out.println("Appium server stopped successfully.");
            } else {
                System.out.println("No Appium server process found to stop.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to stop Appium server.");
        }
    }

    public static void startAppiumServer(int port) throws IOException {
        String command = "appium -p " + port + " --session-override";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.redirectError(ProcessBuilder.Redirect.to(new File("/dev/null")));

        processBuilder.start();
        System.out.println("Appium server started on port " + port);
    }

    public static void stopAppiumServer(int port) throws IOException {
        String command = "lsof -i :" + port + " | grep LISTEN | awk '{print $2}' | xargs kill -9";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.redirectError(ProcessBuilder.Redirect.to(new File("/dev/null")));

        processBuilder.start();
        System.out.println("Appium server stopped on port " + port);
    }

    public static Process startNodeServer(String serverFilePath) throws IOException {
        String command = "node " + serverFilePath;
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.redirectError(ProcessBuilder.Redirect.to(new File("/dev/null")));

        Process process = processBuilder.start();
        System.out.println("Node.js server started for file: " + serverFilePath);

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Node.js Server]: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return process;
    }

    public static void copyFileToSimulator(String deviceName, String platformVersion, String appName, String localFilePath, String localFileNameWithExt) throws IOException {
        localFilePath = localFilePath.concat(localFileNameWithExt);

        // Step 1: Get the simulator UDID using the device name and platform version
        String simulatorUDID = getSimulatorUDID(deviceName, platformVersion);
        if (simulatorUDID == null) {
            throw new IOException("Simulator with the specified device name and platform version not found.");
        }

        // Ensure the simulator is running
        bootSimulatorIfNeeded(simulatorUDID);

        // Step 2: Get the app UUID using the app name
        String appUUID = getAppUUIDFromSimulator(deviceName, platformVersion, appName);
        if (appUUID == null) {
            throw new IOException("App with the specified name not found on the simulator.");
        }

        // Step 3: Construct the destination path inside the simulator's Documents folder
        String simulatorPath = "/Users/your_username/Library/Developer/CoreSimulator/Devices/"
                + simulatorUDID + "/data/Containers/Data/Application/" + appUUID
                + "/Documents/" + localFilePath.substring(localFilePath.lastIndexOf("/") + 1); // Extract the file name from local path

        // Step 4: Prepare and execute the shell command to copy the file (overwrite without asking)
        String command = "cp -f " + localFilePath + " " + simulatorPath; // '-f' forces overwrite
        executeCommand(command);

    }

    public static String getSimulatorUDID(String deviceName, String platformVersion) throws IOException {
        // Run the command to get the list of simulators
        String command = "xcrun simctl list devices";
        Process process = Runtime.getRuntime().exec(command);

        // Wait for the command to complete
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the output of the command
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        // Read the output line by line
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Format the platform version to match the output format (e.g., "(iOS 18.1)")
        String platformInfo = "-- " + "iOS " + platformVersion + " --";

        // Split the output based on the platform version
        String[] sections = output.toString().split(platformInfo);

        if (sections.length < 2) {
            return null;  // If the platform version is not found
        }

        // Get the section corresponding to the specified platform version
        String devicesSection = sections[1];

        // Split the devices section into individual device entries
        String[] devices = devicesSection.split("\n");

        // Iterate over each device entry to find the matching device name and platform version
        for (String device : devices) {
            // Check if the device contains the desired device name and is part of the correct platform version
            if (device.contains(deviceName)) {
                // Extract the UDID from the device entry (it's the value between the parentheses)
                int startIndex = device.indexOf("(") + 1;
                int endIndex = device.indexOf(")", startIndex);
                return device.substring(startIndex, endIndex);
            }
        }

        return null;  // Return null if no matching device was found
    }

    public static String getAppUUIDFromSimulator(String deviceName, String platformVersion, String appName) throws IOException {
        // Get the simulator UDID using the device name and platform version
        String simulatorUDID = getSimulatorUDID(deviceName, platformVersion);
        if (simulatorUDID == null) {
            throw new IOException("Simulator with the specified device name and platform version not found.");
        }

        // Get the app container path for the given app name
        String appContainerCommand = "xcrun simctl get_app_container " + simulatorUDID + " " + "com.swasin." + appName;
        Process process = Runtime.getRuntime().exec(appContainerCommand);

        // Wait for the command to complete
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get the output of the command
        StringBuilder output = new StringBuilder();
        int ch;
        while ((ch = process.getInputStream().read()) != -1) {
            output.append((char) ch);
        }

        String outputString = output.toString();

        // Extract the app UUID from the output
        return extractAppUUIDFromContainerPath(outputString);
    }

    public static String extractAppUUIDFromContainerPath(String containerPath) {
        // Validate the containerPath
        if (containerPath == null || containerPath.isEmpty()) {
            throw new IllegalArgumentException("Container path is null or empty.");
        }

        // Find the start index of the UUID
        int startIndex = containerPath.lastIndexOf("/") + 1;
        if (startIndex == 0 || startIndex >= containerPath.length()) {
            throw new IllegalArgumentException("Invalid container path format. Cannot locate the start of the UUID.");
        }

        // Find the end index of the UUID
        int endIndex = containerPath.indexOf("/", startIndex);
        if (endIndex == -1) {
            // If no `/` exists after the UUID, assume the UUID ends at the end of the string
            endIndex = containerPath.length();
        }

        // Extract and return the UUID
        return containerPath.substring(startIndex, endIndex);
    }

    public static void executeCommand(String command) throws IOException {
        // Execute the command to boot the simulator
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void bootSimulatorIfNeeded(String simulatorUDID) throws IOException {
        // Run the command to list devices
        String command = "xcrun simctl list devices";
        Process process = Runtime.getRuntime().exec(command);

        // Capture the output of the command
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for the command to complete
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the simulator is booted
        String outputString = output.toString();
        if (!outputString.contains(simulatorUDID) || !outputString.contains("(Booted)")) {
            // Boot the simulator if it's not booted
            System.out.println("Booting the simulator...");
            command = "xcrun simctl boot " + simulatorUDID;
            executeCommand(command);
        } else {
            System.out.println("Simulator is already booted.");
        }

        System.out.println("Simulator booted.");
    }

    public static AppiumDriver setCapabilities(String deviceName, String platformVersion, String appName) throws Exception {

        // Get the simulator ID (UDID)
        String simulatorId = getSimulatorUDID(deviceName, platformVersion);

        if (simulatorId == null || simulatorId.isEmpty()) {
            throw new IllegalArgumentException("Simulator ID cannot be null or empty.");
        }

        // Boot the simulator if needed
        bootSimulatorIfNeeded(simulatorId);

        // Set the Desired Capabilities
        XCUITestOptions options = new XCUITestOptions()
                .setAutomationName("XCUITest")
                .setBundleId(String.format("com.swasin.%s", appName))
                .setWdaLaunchTimeout(Duration.ofMillis(180000))
                .setDeviceName(deviceName)
                .setPlatformVersion(platformVersion);

        // Initialize the driver
        AppiumDriver driver = null;

        options.getBundleId().ifPresent(bundleId -> {
            CommonMethods.app_name = bundleId.substring(bundleId.lastIndexOf(".") + 1);
        });

        // Launch the app (without re-launching it again)
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/"), options);
        System.out.println("App launched successfully on iOS Simulator!");

        return driver;
    }

    public static void selectFileInSimulator(AppiumDriver driver, String fileName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        //searching the file in simulator
        By searchField = AppiumBy.accessibilityId("Search");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
        driver.findElement(searchField).sendKeys(fileName);

        By searchButton = AppiumBy.xpath("//XCUIElementTypeButton[@name=\"Search\"]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchButton));
        driver.findElement(searchButton).click();

        By fileToSelect = AppiumBy.xpath(String.format("(//XCUIElementTypeStaticText[@name='%s'])[2]", fileName));
        wait.until(ExpectedConditions.visibilityOfElementLocated(fileToSelect));

        int centerX = getCenter(driver.findElement(fileToSelect)).x;
        int centerY = getCenter(driver.findElement(fileToSelect)).y;

        // Create a PointerInput instance
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");

        // Create a sequence of actions (press -> move -> release)
        Sequence sequence = new Sequence(finger1, 1)
                .addAction(finger1.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), centerX, centerY)) // Move to the center
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg())) // Press down
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg())) // Press down
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg())); // Release (tap)

        // Perform the action
        driver.perform(Collections.singletonList(sequence));
    }

    public static Point getCenter(WebElement element) {
        //get location of the element
        Point location = element.getLocation();

        //get dimensions (widht and height) of the element
        Dimension dimension = element.getSize();

        //get center point of the element
        Point center = new Point(location.x + dimension.width / 2, location.y + dimension.height / 2);

        return center;
    }

    public static void resetSimulatorAccess(String deviceName, String platformVersion) throws Exception {

        String simulatorId = getSimulatorUDID(deviceName, platformVersion);
        bootSimulatorIfNeeded(simulatorId);

        if (simulatorId == null || simulatorId.isEmpty()) {
            throw new IllegalArgumentException("Simulator ID cannot be null or empty.");
        }

        // Construct the command
        String command = "xcrun simctl privacy " + simulatorId + " reset all";

        // Execute the command
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture and print the output of the command
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output for debugging
            }
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }

        System.out.println("Privacy settings reset successfully for simulator ID: " + simulatorId);
    }

    public static void grantMicrophoneAccess(String deviceName, String platformVersion, String appName) throws Exception {

        String simulatorId = getSimulatorUDID(deviceName, platformVersion);
        bootSimulatorIfNeeded(simulatorId);

        if (simulatorId == null || simulatorId.isEmpty()) {
            throw new IllegalArgumentException("Simulator ID cannot be null or empty.");
        }

        // Construct the command
        String command = "xcrun simctl privacy " + simulatorId + " grant microphone com.swasin." + appName;

        // Execute the command
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture and print the output of the command
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print the output for debugging
            }
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }

        System.out.println("Microphone access granted for simulator ID: " + simulatorId);
    }

    public static void copyFileFromSimulatorToResources(String simulatorFilePath, String projectResourcesPath) throws IOException {
        // Validate if the simulator file path exists
        File sourceFile = new File(simulatorFilePath);
        if (!sourceFile.exists()) {
            throw new IOException("The source file does not exist: " + simulatorFilePath);
        }

        // Ensure that the target directory exists in the resources folder
        Path targetDir = Paths.get(projectResourcesPath);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // Define the target file path in the resources folder
        File targetFile = new File(targetDir.toFile(), "audio.wav");

        // Copy the file from simulator to the target directory
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(targetFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("File successfully copied to: " + targetFile.getAbsolutePath());
        }
    }

    public static void clearAppScreenshotsDirectory() {

        String projectRoot = Paths.get(System.getProperty("user.dir")).toString();
        String directoryPath = projectRoot + String.format("/screenshots/%s/", app_name);

        Path path = Paths.get(directoryPath);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return;
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directory cleared successfully: " + directoryPath);
        } catch (IOException e) {
            System.err.println("Error clearing directory: " + e.getMessage());
        }
    }

    public static void selectPickerValueLinearSearch(AppiumDriver driver, WebElement picker, String targetValue) {
        String currentValue = picker.getAttribute("value");

        // Scroll until the desired value is selected
        while (!currentValue.equals(targetValue)) {
            Map<String, Object> params = new HashMap<>();
            params.put("order", "next"); // Use "previous" for reverse direction
            params.put("offset", 0.15);  // Adjust the scroll offset as needed
            params.put("element", ((RemoteWebElement) picker).getId());
            driver.executeScript("mobile: selectPickerWheelValue", params);

            // Update current value after scrolling
            currentValue = picker.getAttribute("value");

            // Optional: Add a timeout or break condition to avoid infinite loops
            System.out.println("Current Value: " + currentValue);
        }

        System.out.println("Selected Value: " + currentValue);
    }

    public static void selectPickerValueBinarySearch(AppiumDriver driver, WebElement picker, String targetValue) {
        try {
            // Parse the current and target values
            int target = Integer.parseInt(targetValue);
            int current = Integer.parseInt(picker.getAttribute("value"));

            // Set initial offset and direction
            double offset = 0.3; // Default offset
            String direction = "next"; // Default scrolling direction
            int attempts = 0;
            int maxAttempts = 20;

            while (current != target && attempts < maxAttempts) {
                // Determine the direction based on the target year
                direction = (current > target) ? "previous" : "next";

                // Adjust offset dynamically
                int difference = Math.abs(current - target);
                if (difference >= 50) {
                    offset = 0.5; // Max offset for large differences
                } else if (difference >= 10) {
                    offset = 0.4; // Medium offset for moderate differences
                } else {
                    offset = 0.2; // Small offset for fine adjustments
                }

                // Debugging logs
                System.out.println("Current: " + current + ", Target: " + target + ", Offset: " + offset + ", Direction: " + direction);

                // Scroll the picker wheel
                Map<String, Object> params = new HashMap<>();
                params.put("order", direction);
                params.put("offset", offset);
                params.put("element", ((RemoteWebElement) picker).getId());
                driver.executeScript("mobile: selectPickerWheelValue", params);

                // Update current value
                current = Integer.parseInt(picker.getAttribute("value"));
                attempts++;
            }

            if (current != target) {
                throw new RuntimeException("Failed to select the target year: " + targetValue);
            }

            System.out.println("Successfully selected year: " + targetValue);
        } catch (Exception e) {
            System.err.println("Error during year selection: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let the calling code handle it
        }
    }

}
