package org.duguo.xdir.tests.functional.admin;

import org.duguo.xdir.tests.functional.AbstractJcrIT;
import org.duguo.xdir.tests.functional.AbstractUIIT;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import static org.testng.Assert.fail;

public abstract class AbstractAdminIT extends AbstractJcrIT {
    private static Logger LOG = LoggerFactory.getLogger(AbstractAdminIT.class);

    protected static String _webAdminUrl = "https://localhost:8443/admin/index.html";

    @BeforeSuite(dependsOnMethods = {"initWebDriver"})
    @Parameters({"test.login.username", "test.login.password"})
    public void login(@Optional("superuser") String loginUsername, @Optional("superuser") String loginPassword) {
        detectAdminUrl();
        goAdmin();
        performLogin(loginUsername, loginPassword);
    }

    private void performLogin(String loginUsername, String loginPassword) {
        WebElement passwordElement = findElementByName("password");
        findElementByName("username").sendKeys(loginUsername);
        passwordElement.sendKeys(loginPassword);
        passwordElement.submit();
        assertXDirPageNormalStatus();

        findLinkByText("Platform");
    }

    private void detectAdminUrl() {
        goHome();
        try {
            _webAdminUrl = _webDriver.findElement(By.linkText("Admin")).getAttribute("href");
        } catch (NoSuchElementException ignore) {
        }
    }

    protected void goAdmin() {
        goUrl(_webAdminUrl);
    }

    protected void goAdminUrl(String relativePath) {
        String adminUrl=_webAdminUrl.substring(0,_webAdminUrl.lastIndexOf("/"))+relativePath;
        goUrl(adminUrl);
    }
}
