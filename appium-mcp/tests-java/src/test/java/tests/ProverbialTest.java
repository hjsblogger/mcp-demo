package tests;

import base.BaseTest;
import pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProverbialTest extends BaseTest {

    @Test
    public void testProverbialFlow() {
        HomePage homePage = new HomePage(driver, wait);

        homePage.clickColor();
        homePage.clickText();
        homePage.clickToast();

        Assert.assertFalse(homePage.isToastVisible(), "Toast message should not be visible");
    }
}
