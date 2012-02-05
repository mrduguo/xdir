package org.duguo.xdir.tests.functional;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import static org.testng.Assert.*;

@Listeners(ITListener.class)
public abstract class AbstractUIIT {
    private static Logger LOG = LoggerFactory.getLogger(AbstractUIIT.class);

    protected static WebDriver _webDriver;
    protected static String _webHomeUrl;
    protected static long _thinkTime;
    private static long _previousScreenshotTimestamp = 0;

    // -Dtest.web.driver.impl=org.openqa.selenium.firefox.FirefoxDriver
    // -Dtest.web.driver.impl=org.openqa.selenium.htmlunit.HtmlUnitDriver
    @BeforeSuite
    @Parameters({"test.web.driver.impl", "test.web.home.url", "test.web.think.time"})
    public void initWebDriver(@Optional("org.openqa.selenium.htmlunit.HtmlUnitDriver") String webDriverImpl, @Optional("http://localhost:8080/index.html") String webHomeUrl, @Optional("10") long thinkTime) throws Exception {
        redirectJulToSlf4j();

        _webHomeUrl = webHomeUrl;
        _thinkTime = thinkTime;
        _webDriver = (WebDriver) Class.forName(webDriverImpl).newInstance();

        goHome();
    }

    @AfterSuite
    public void destroyWebDriver() {
        _webDriver.close();
        _webDriver = null;
    }

    protected void goHome() {
        goUrl(_webHomeUrl);
    }

    protected static void goUrl(String url) {
        LOG.info("going: {}", url);
        _webDriver.navigate().to(url);
        assertXDirPageNormalStatus();
    }

    protected WebElement findLinkByText(String linkText) {
        return findElementBy(By.linkText(linkText));
    }

    protected WebElement findElementById(String id) {
        return findElementBy(By.id(id));
    }

    protected WebElement findElementByCss(String cssSelector) {
        return findElementBy(By.cssSelector(cssSelector));
    }

    protected WebElement findElementByName(String name) {
        return findElementBy(By.name(name));
    }

    protected WebElement findElementBy(By by) {
        return _webDriver.findElement(by);
    }


    protected boolean linkExist(String linkText) {
        return _webDriver.findElements(By.linkText(linkText)).size()>0;
    }



    protected void changeInputElementText(Object elementOrName,String value) {
        WebElement inputElement;
        if(elementOrName instanceof  WebElement){
            inputElement=(WebElement)elementOrName;
        } else{
            inputElement = findElementByName((String)elementOrName);
        }
        inputElement.clear();
        inputElement.sendKeys(value);
    }



    protected void waitForAjaxRequestFinish() {
        (new WebDriverWait(_webDriver, 5)).until(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver webDriver) {
                JavascriptExecutor js=(JavascriptExecutor) webDriver;
                Boolean noActiveRequest =(Boolean)js.executeScript("return $.active==0;");
                if(noActiveRequest){
                    return noActiveRequest;
                }else{
                    return null;
                }
            }
        });
    }

    protected boolean pageSourceContains(String text) {
        return _webDriver.getPageSource().contains(text);
    }


    protected void clickOnLink(String linkText) {
        LOG.info("clicking link: {}", linkText);
        findLinkByText(linkText).click();
        assertXDirPageNormalStatus();
    }

    protected static void assertPageSourceCodeContains(String text) {
        if (!_webDriver.getPageSource().contains(text)) {
            fail("cannot find [" + text + "] on page source");
        }
    }

    protected static void assertXDirPageNormalStatus() {
        try {
            String pageSourceCode = _webDriver.getPageSource();
            if (!pageSourceCode.contains("Powered by XDir")) {
                fail("page source code  doesn't contain link [Powered by XDir]:"+pageSourceCode);
            }
            if (pageSourceCode.contains("HTTP 500 - Internal Server Error")) {
                fail("HTTP 500 - Internal Server Error");
            }
            try {
                Thread.sleep(_thinkTime);
            } catch (Exception ex) {
                throw new RuntimeException("think failed :(", ex);
            }
        } finally {
            takeScreenshot();
        }
    }

    private static void takeScreenshot() {
        if (_webDriver instanceof TakesScreenshot) {
            try {
                if (_previousScreenshotTimestamp == 0) {
                    _previousScreenshotTimestamp = Reporter.getCurrentTestResult().getStartMillis();
                }
                File scrFile = ((TakesScreenshot) _webDriver).getScreenshotAs(OutputType.FILE);
                String targetFilePath = System.getProperty("basedir", "tests/functional")
                        + "/target/screenshot/test-"
                        + Reporter.getCurrentTestResult().getMethod().getTestClass().getName()
                        + "." + Reporter.getCurrentTestResult().getMethod().getMethodName()
                        + "-" + System.currentTimeMillis()
                        + "-" + (System.currentTimeMillis() - _previousScreenshotTimestamp) + ".png";
                File targetFile = new File(targetFilePath);
                FileUtils.copyFile(scrFile, targetFile);
                _previousScreenshotTimestamp = System.currentTimeMillis();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void redirectJulToSlf4j() {
        //http://blog.cn-consult.dk/2009/03/bridging-javautillogging-to-slf4j.html
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        SLF4JBridgeHandler.install();
    }
}
