package org.duguo.xdir.tests.functional.demo;

import org.duguo.xdir.tests.functional.AbstractUIIT;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class CrawlPublicLinksIT extends AbstractUIIT {
    
    private static Logger LOG= LoggerFactory.getLogger(CrawlPublicLinksIT.class);

    @Test(parameters = {"test.public.exclude.paths","test.public.supported.formats"})
    public void clickOnPublicLink(@Optional("/profiles/") String excludePaths,@Optional(".html,.xhtml") String supportedFormats) {
        goHome();
        ArrayList<String> visitedLinks = crawlLinks(_webHomeUrl, excludePaths, supportedFormats);
        LOG.info("crawled {} links",visitedLinks.size());
    }

    public static ArrayList<String> crawlLinks(String baseUrl, String excludePaths, String supportedFormats) {
        if(baseUrl.endsWith("index.html"))
            baseUrl=baseUrl.substring(0,baseUrl.length()-"index.html".length());

        ArrayList<String> visitedLinks = new ArrayList<String>();
        internalCrawlLinks(baseUrl, excludePaths.split(","), supportedFormats.split(","), new ArrayList<String>(), visitedLinks);
        return visitedLinks;
    }

    private static void internalCrawlLinks(String baseUrl, String[] excludedPathsList, String[] supportedFormats, List<String> allLinks, List<String> visitedLinks) {
        collectLinks(baseUrl, excludedPathsList,supportedFormats,  allLinks,  visitedLinks);
        boolean clickedLink = false;
        for (String currentLink : allLinks) {
            if (!visitedLinks.contains(currentLink)) {
                clickedLink = true;
                visitedLinks.add(currentLink);
                goUrl(currentLink);
                break;
            }
        }
        if (clickedLink) {
            internalCrawlLinks((String) baseUrl, (String[]) excludedPathsList, supportedFormats, allLinks, visitedLinks);
        }
    }

    private static void collectLinks(String baseUrl, String[] excludedPathsList,String[] supportedFormats, List<String> allLinks, List<String> visitedLinks) {
        List<WebElement> currentPageLinks = _webDriver.findElements(By.xpath("//a"));
        for (WebElement link : currentPageLinks) {
            String linkHref = link.getAttribute("href");
            if (isLinkIncluded(baseUrl, linkHref, supportedFormats)) {
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

    private static boolean isLinkIncluded(String baseUrl, String linkHref,String[] supportedFormats) {
        if(linkHref.startsWith(baseUrl) && linkHref.indexOf("?")<0){
            for (String format : supportedFormats) {
                if(linkHref.endsWith(format)){
                    return true;
                }
            }
        }
        return false;
    }


}
