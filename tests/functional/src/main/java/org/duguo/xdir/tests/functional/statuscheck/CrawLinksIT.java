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

import java.util.ArrayList;
import java.util.List;

public class CrawLinksIT extends AbstractUIIT {
    
    private static Logger LOG= LoggerFactory.getLogger(CrawLinksIT.class);

    private List<String> allLinks = new ArrayList<String>();
    private List<String> visitedLinks = new ArrayList<String>();

    @Test(parameters = {"test.public.exclude.paths"})
    public void clickOnPublicLink(@Optional("/admin/,/profiles/") String excludePaths) {
        String[] excludedPathsList = excludePaths.split(",");
        crawLinks(_webBaseUrl, excludedPathsList);
    }

    @Test(dependsOnMethods = {"clickOnPublicLink"},  parameters = {"test.login.username", "test.login.password"})
    public void login(@Optional("superuser") String loginUsername, @Optional("superuser") String loginPassword) {
        goHome();
        clickOnLink("Admin");
        WebElement passwordElement = _webDriver.findElement(By.name("password"));
        _webDriver.findElement(By.name("username")).sendKeys(loginUsername);
        passwordElement.sendKeys(loginPassword);
        passwordElement.submit();
        assertXDirPageNormalStatus();
    }

    @Test(dependsOnMethods = {"login"}, parameters = {"test.private.exclude.paths"})
    public void clickOnPrivateLink(@Optional("/sites/,/login/,/fs/,/jdbc/,/resources/template/,/osgi/bundles/,/osgi/services/,/osgi/spring/,/admin/resources/jcr/,/account/logout.html") String excludePaths) {
        String[] excludedPathsList = excludePaths.split(",");
        crawLinks("https://", excludedPathsList);
    }

    private void crawLinks(String baseUrl, String[] excludedPathsList) {
        collectLinks(baseUrl, excludedPathsList);
        boolean clickedLink = false;
        for (String currentLink : allLinks) {
            if (!visitedLinks.contains(currentLink)) {
                clickedLink = true;
                visitedLinks.add(currentLink);
                LOG.info("crawling: {}", currentLink);
                _webDriver.navigate().to(currentLink);
                assertXDirPageNormalStatus();
                break;
            }
        }
        if (clickedLink) {
            crawLinks(baseUrl, excludedPathsList);
        }
    }

    private void collectLinks(String baseUrl, String[] excludedPathsList) {
        List<WebElement> currentPageLinks = _webDriver.findElements(By.xpath("//a"));
        for (WebElement link : currentPageLinks) {
            String linkHref = link.getAttribute("href");
            if (linkHref.startsWith(baseUrl) && linkHref.endsWith(".html") && linkHref.indexOf("?")<0) {
                boolean isExcluded = false;
                for (String currentExclude : excludedPathsList) {
                    if (linkHref.indexOf(currentExclude) >= 0) {
                        isExcluded = true;
                    }
                }
                if (!isExcluded && !allLinks.contains(linkHref)) {
                    allLinks.add(linkHref);
                }
            }
        }
    }


}
