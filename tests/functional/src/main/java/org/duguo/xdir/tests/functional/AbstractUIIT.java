package org.duguo.xdir.tests.functional;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public abstract class AbstractUIIT {

    protected static WebDriver _webDriver;
    protected static String _webBaseUrl;
    protected static long _thinkTime;

    @BeforeSuite
    @Parameters({"test.web.driver.impl", "test.web.base.url", "test.web.think.time"})
    public void initWebDriver(@Optional("org.openqa.selenium.htmlunit.HtmlUnitDriver") String webDriverImpl, @Optional("http://localhost:8080/xdir") String webBaseUrl, @Optional("0") long thinkTime) throws Exception {
        _webBaseUrl = webBaseUrl;
        _thinkTime = thinkTime;
        _webDriver = (WebDriver) Class.forName(webDriverImpl).newInstance();
        _webDriver.navigate().to(_webBaseUrl);
        assertXDirPageNormalStatus();
    }

    @AfterSuite
    public void destroyWebDriver() {
        _webDriver.close();
        _webDriver = null;
    }

    protected void goHome() {
        clickOnLink("Home");
    }


    protected void clickOnLink(String linkText) {
        _webDriver.findElement(By.linkText(linkText)).click();
        assertXDirPageNormalStatus();
    }

    protected void assertXDirPageNormalStatus() {
        try {
            String pageSourceCode = _webDriver.getPageSource();
            try {
                _webDriver.findElement(By.linkText("Powered by XDir"));
                if (pageSourceCode.contains("HTTP 500 - Internal Server Error")) {
                    fail("HTTP 500 - Internal Server Error. Page Source Code [" + pageSourceCode + "]");
                }
            } catch (NoSuchElementException ex) {
                fail("Page Source Code [" + pageSourceCode + "] doesn't contain link [Powered by XDir]");
            }
            try {
                Thread.sleep(_thinkTime);
            } catch (Exception ex) {
                throw new RuntimeException("Think failed :(", ex);
            }
        } finally {
            takeScreenshot();
        }
    }

    private void takeScreenshot() {
        if (_webDriver instanceof TakesScreenshot) {
            try {
                File scrFile = ((TakesScreenshot) _webDriver).getScreenshotAs(OutputType.FILE);
                String targetFilePath = System.getProperty("basedir", "tests/functional") + "/target/screenshot/test-" + System.currentTimeMillis() + ".png";
                File targetFile = new File(targetFilePath);
                FileUtils.copyFile(scrFile, targetFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
