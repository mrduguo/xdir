package org.duguo.xdir.tests.functional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.testng.Assert.assertFalse;

public abstract class AbstractJcrIT extends AbstractUIIT {
    private static Logger LOG = LoggerFactory.getLogger(AbstractJcrIT.class);

    protected void attachTmpTextFile() throws Exception {
        clickOnLink("Attach");
        File tmpFile = File.createTempFile("tmp", ".txt");
        try {
            String fileContent = "TEMP_CONTENT_" + System.currentTimeMillis();
            FileUtils.writeStringToFile(tmpFile, fileContent);
            findElementByName("uploadedfile").sendKeys(tmpFile.getAbsolutePath());
            findElementByName("_path").sendKeys("tmp.txt");
            findElementById("buttonSave").submit();
            assertPageSourceCodeContains("File attached!");

            clickOnLink("Finish");
            clickOnLink("tmp.txt");
            if(linkExist("jcr:content")){
                clickOnLink("jcr:content");
            }
            assertPageSourceCodeContains(fileContent);
        } finally {
            tmpFile.delete();
        }
    }



    protected void deleteNode(String nodeName) {
        clickOnLink(nodeName);
        clickOnLink("Delete");
        if(pageSourceContains("Delete All")) {
            findElementById("buttonDeleteAll").submit();
        }else{
            findElementById("buttonDelete").submit();
        }
        clickOnLink("Ok");
        assertFalse(linkExist(nodeName));
    }
}
