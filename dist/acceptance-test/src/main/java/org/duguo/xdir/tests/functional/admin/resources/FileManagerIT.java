package org.duguo.xdir.tests.functional.admin.resources;

import org.duguo.xdir.tests.functional.ITListener;
import org.duguo.xdir.tests.functional.admin.AbstractAdminIT;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FileManagerIT extends AbstractAdminIT {

    @Test
    public void navigateToAppLicenseTxtFile() {
        goAdminUrl("/resources/fs/index.html");
        clickOnLink("App");
        clickOnLink("LICENSE.txt");
        assertPageSourceCodeContains("TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION");
    }

    @Test(dependsOnMethods = {"navigateToAppLicenseTxtFile"})
    public void attachTmpFileToAppVarFolder() throws Exception {
        clickOnLink("var");
        attachTmpTextFile();
    }

    @Test(dependsOnMethods = {"attachTmpFileToAppVarFolder"})
    public void tryInlineEditFileWithRemoteWebDriver() throws Exception {
        if (_webDriver instanceof RemoteWebDriver) {
            WebElement editableField=findElementByCss("pre.editable");
            Actions builder = new Actions(_webDriver);
            builder.doubleClick(editableField);
            builder.build().perform();

            String newContent = "TEMP_CONTENT_" + System.currentTimeMillis();
            changeInputElementText(editableField,newContent);
            editableField.sendKeys(Keys.TAB);
            waitForAjaxRequestFinish();

            goAdminUrl("/resources/fs/app/var/tmp.txt.html");
            assertPageSourceCodeContains(newContent);
        }else{
            ITListener.softSkippedTest();
        }
    }

    @Test(dependsOnMethods = {"tryInlineEditFileWithRemoteWebDriver"})
    public void overrideExistingTmpTextFile() throws Exception {
        clickOnLink("var");
        attachTmpTextFile();
    }

    @Test(dependsOnMethods = {"overrideExistingTmpTextFile"})
    public void deleteExistingTmpTextFile() throws Exception {
        clickOnLink("tmp.txt");
        clickOnLink("Delete");
        findElementById("buttonDelete").submit();
        assertPageSourceCodeContains("Node deleted!");
        clickOnLink("Ok");
        assertEquals(_webDriver.findElements(By.linkText("tmp.txt")).size(), 0);
    }
}
