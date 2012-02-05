package org.duguo.xdir.tests.functional.admin.resources;

import org.apache.commons.io.FileUtils;
import org.duguo.xdir.tests.functional.ITListener;
import org.duguo.xdir.tests.functional.admin.AbstractAdminIT;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class JcrManagerIT extends AbstractAdminIT {


    private static Logger LOG = LoggerFactory.getLogger(JcrManagerIT.class);

    private String testJcrNodeName;

    @Test
    public void navigateToDefaultJcrWorkspace() {
        goAdminUrl("/resources/jcr/index.html");
        clickOnLink("xdir");
        clickOnLink("default");
    }

    @Test(dependsOnMethods = {"navigateToDefaultJcrWorkspace"})
    public void createTestNodeAtRoot() throws Exception {
        testJcrNodeName = "test_" + System.currentTimeMillis();
        clickOnLink("New");
        findElementByName("_title").sendKeys(testJcrNodeName + "_title");
        findElementByName("_path").sendKeys(testJcrNodeName);
        findElementByName("propertiesnew1_name").sendKeys("test property");
        findElementByName("propertiesnew1_value").sendKeys("test property value");
        findElementById("buttonSave").submit();
        clickOnLink("Finish");
        clickOnLink(testJcrNodeName);
    }

    @Test(dependsOnMethods = {"createTestNodeAtRoot"})
    public void tryInlineEditNodeWithRemoteWebDriver() throws Exception {
        if (_webDriver instanceof RemoteWebDriver) {
            WebElement editableField = findElementById("inline_editor_test_property");
            Actions builder = new Actions(_webDriver);
            builder.release().doubleClick(editableField);
            builder.build().perform();

            String newContent = "TEMP_CONTENT_" + System.currentTimeMillis();

            changeInputElementText(editableField, newContent);
            editableField.sendKeys(Keys.TAB);
            waitForAjaxRequestFinish();

            clickOnLink(testJcrNodeName);
            assertPageSourceCodeContains(newContent);
        } else {
            ITListener.softSkippedTest();
        }
    }

    @Test(dependsOnMethods = {"tryInlineEditNodeWithRemoteWebDriver"})
    public void editNode() throws Exception {
        clickOnLink("Edit");
        changeInputElementText("_title", testJcrNodeName + "_title_new");

        String newContent = "TEMP_CONTENT_" + System.currentTimeMillis();
        changeInputElementText("propertiesexisttest_property_value",newContent);
        findElementById("buttonSave").submit();
        clickOnLink("Ok");
        clickOnLink(testJcrNodeName);
        assertPageSourceCodeContains(newContent);
    }


    @Test(dependsOnMethods = {"editNode"})
    public void attachFile() throws Exception {
        attachTmpTextFile();
    }

    @Test(dependsOnMethods = {"attachFile"})
    public void copyNode() throws Exception {
        clickOnLink("tmp.txt");
        clickOnLink("Copy");
        changeInputElementText("_path","tmp-copy.txt");
        changeInputElementText("_title", "tmp copy txt");
        findElementById("buttonCopy").submit();
        clickOnLink("Finish");
        clickOnLink("tmp-copy.txt");
        assertPageSourceCodeContains("tmp copy txt");
    }

    @Test(dependsOnMethods = {"copyNode"})
    public void moveNode() throws Exception {
        clickOnLink("Move");
        changeInputElementText("_path", "tmp-move.txt");
        findElementById("buttonMove").submit();
        clickOnLink("Finish");
        clickOnLink("tmp-move.txt");
    }

    @Test(dependsOnMethods = {"moveNode"})
    public void exportAndImportNode() throws Exception {
        String jcrXmlContent= exportNodeToTmpFileAndDeleteExsitingNode();


        importJcrXmlFileToSameParent(jcrXmlContent);
    }

    private String exportNodeToTmpFileAndDeleteExsitingNode() {
        String testFolderLink=findLinkByText(testJcrNodeName).getAttribute("href");
        clickOnLink("tmp.txt");
        String downloadUrl=findLinkByText("Export").getAttribute("href");
        downloadUrl=downloadUrl.substring(0,downloadUrl.indexOf("?"));
        downloadUrl=downloadUrl+"?format=xml";
        LOG.info("going; {}",downloadUrl);
        _webDriver.navigate().to(downloadUrl);

        String jcrXmlContent=_webDriver.getPageSource();
        assertPageSourceCodeContains("sv:name=\"tmp.txt\"");
        goUrl(testFolderLink);
        deleteNode("tmp.txt");
        return jcrXmlContent;
    }


    private void importJcrXmlFileToSameParent(String jcrXmlContent) throws IOException {
        clickOnLink("Import");
        File tmpFile = File.createTempFile("tmp", "-jcr.xml");
        try {
            FileUtils.writeStringToFile(tmpFile, jcrXmlContent);
            findElementByName("uploadedfile").sendKeys(tmpFile.getAbsolutePath());
            findElementById("buttonImport").submit();
            assertPageSourceCodeContains("Imported!");

            clickOnLink("Finish");
            clickOnLink("tmp.txt");
            clickOnLink("jcr:content");

        } finally {
            tmpFile.delete();
        }
    }

    @Test(dependsOnMethods = {"exportAndImportNode"})
    public void deleteTestNodeAtRoot() throws Exception {
        deleteNode(testJcrNodeName);
    }
}
