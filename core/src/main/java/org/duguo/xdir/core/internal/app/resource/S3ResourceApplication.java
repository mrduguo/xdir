package org.duguo.xdir.core.internal.app.resource;

import org.duguo.xdir.core.internal.model.ModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class S3ResourceApplication extends ResourceApplication {
    private static final Logger logger = LoggerFactory.getLogger(S3ResourceApplication.class);

    private String awsS3BaseUrl;
    private boolean isS3Connected = false;
    private boolean isS3Verified = false;


    public String getResourceUrl(ModelImpl model, String resourceName) {
        if (isS3Connected) {
            return buildS3ResourceUrl(resourceName);
        } else if (!isS3Verified) {
            isS3Verified = true;
            String s3ResourceUrl = buildS3ResourceUrl(resourceName);
            try {
                new URL(s3ResourceUrl).openConnection().getContent();
                isS3Connected = true;
                logger.info("s3 connected, will serve resource via s3");
                return s3ResourceUrl;
            } catch (Exception notConnected) {
                logger.warn("s3 not connected");
            }
        }
        return super.getResourceUrl(model, resourceName);
    }

    private String buildS3ResourceUrl(String resourceName) {
        StringBuilder resourceUrl = new StringBuilder();
        resourceUrl.append(awsS3BaseUrl);
        resourceUrl.append("/");
        appendCacheTimestamp(resourceUrl);
        resourceUrl.append("/");
        resourceUrl.append(resourceName);
        return resourceUrl.toString();
    }

    public void setAwsS3BaseUrl(String awsS3BaseUrl) {
        this.awsS3BaseUrl = awsS3BaseUrl;
    }
}
