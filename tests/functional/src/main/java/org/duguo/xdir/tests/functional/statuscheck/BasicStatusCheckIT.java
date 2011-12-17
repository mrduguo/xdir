package org.duguo.xdir.tests.functional.statuscheck;

import org.duguo.xdir.tests.functional.AbstractUIIT;
import org.duguo.xdir.tests.functional.DataProviderArguments;
import org.duguo.xdir.tests.functional.FileDataProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class BasicStatusCheckIT extends AbstractUIIT {

    private static Logger LOG= LoggerFactory.getLogger(BasicStatusCheckIT.class);

    @Test(dataProviderClass = FileDataProvider.class, dataProvider = "getStringListFromFile")
    @DataProviderArguments("resourcePath=/testng/data/public-links.txt")
    public void clickOnPublicLink(String linkText) {
        clickLinkByText(linkText);
        assertXDirPageNormalStatus();
    }

    @Test(dependsOnMethods = {"clickOnPublicLink"}, parameters = {"test.login.username", "test.login.password"})
    public void login(@Optional("superuser") String loginUsername, @Optional("superuser") String loginPassword) {
        goHome();
        clickOnLink("Admin");
        WebElement passwordElement = _webDriver.findElement(By.name("password"));
        _webDriver.findElement(By.name("username")).sendKeys(loginUsername);
        passwordElement.sendKeys(loginPassword);
        passwordElement.submit();
        assertXDirPageNormalStatus();
    }

    @Test( dependsOnMethods = {"login"}, dataProviderClass = FileDataProvider.class, dataProvider = "getStringListFromFile")
    @DataProviderArguments("resourcePath=/testng/data/private-links.txt")
    public void clickOnPrivateLink(String linkText) {
        clickLinkByText(linkText);
        assertXDirPageNormalStatus();
    }

    private void clickLinkByText(String linkText) {
        WebElement linkElement = _webDriver.findElement(By.linkText(linkText));
        LOG.info("click link {}: {}", linkText, linkElement.getAttribute("href"));
        linkElement.click();
    }


}
