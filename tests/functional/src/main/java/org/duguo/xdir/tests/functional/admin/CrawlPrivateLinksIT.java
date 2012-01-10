package org.duguo.xdir.tests.functional.admin;

import org.duguo.xdir.tests.functional.demo.CrawlPublicLinksIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class CrawlPrivateLinksIT extends AbstractAdminIT {
    private static Logger LOG = LoggerFactory.getLogger(CrawlPrivateLinksIT.class);

    @Test(parameters = {"test.private.exclude.paths","test.public.supported.formats"})
    public void clickOnPrivateLink(@Optional("/sites/,/login/,/fs/app/bundles/,/fs/app/data/,/fs/app/var/,/jdbc/,/resources/template/,/osgi/bundles/,/osgi/services/,/osgi/spring/,/admin/resources/jcr,/account/logout.html") String excludePaths,@Optional(".html,.xhtml") String supportedFormats) {
        goAdmin();
        ArrayList<String> visitedLinks = CrawlPublicLinksIT.crawlLinks(_webAdminUrl, excludePaths, supportedFormats);
        LOG.info("crawled {} links",visitedLinks.size());
    }

}
