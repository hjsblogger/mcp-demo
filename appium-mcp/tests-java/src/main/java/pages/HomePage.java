package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    @AndroidFindBy(id = "com.lambdatest.proverbial:id/color")
    private WebElement colorButton;

    @AndroidFindBy(id = "com.lambdatest.proverbial:id/Text")
    private WebElement textButton;

    @AndroidFindBy(id = "com.lambdatest.proverbial:id/toast")
    private WebElement toastButton;

    public HomePage(AndroidDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public void clickColor() {
        wait.until(ExpectedConditions.elementToBeClickable(colorButton)).click();
    }

    public void clickText() {
        wait.until(ExpectedConditions.elementToBeClickable(textButton)).click();
    }

    public void clickToast() {
        wait.until(ExpectedConditions.elementToBeClickable(toastButton)).click();
    }

    public boolean isToastVisible() {
        return !driver.findElements(AppiumBy.xpath("//android.widget.Toast")).isEmpty();
    }
}
