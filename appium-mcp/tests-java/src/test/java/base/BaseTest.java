package base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.URL;
import java.time.Duration;

public class BaseTest {

    protected AndroidDriver driver;
    protected WebDriverWait wait;

    @BeforeClass
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", "Galaxy S24");
        caps.setCapability("appium:platformVersion", "14");
        caps.setCapability("appium:app", "lt://APP1016025801781796939639692");
        caps.setCapability("appium:autoAcceptAlerts", true);
        caps.setCapability("appium:autoGrantPermissions", true);

        String ltUsername = System.getenv("LT_USERNAME");
        String ltAccessKey = System.getenv("LT_ACCESS_KEY");
        URL hubUrl = new URL("https://" + ltUsername + ":" + ltAccessKey + "@mobile-hub.lambdatest.com/wd/hub");

        driver = new AndroidDriver(hubUrl, caps);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
