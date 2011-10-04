package org.duguo.xdir.core.internal.app.resource;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.QueryStringAuthGenerator;
import com.amazon.s3.S3Object;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


public class S3ResourceApplication extends ResourceApplication {
    private static final Logger logger = LoggerFactory.getLogger(S3ResourceApplication.class);

    private String cacheTimestamp;

    private String awsS3BaseUrl;
    private String awsBucketName;
    private String awsAccessKeyId;
    private String awsSecretAccessKey;

    public S3ResourceApplication() {
        resetCacheTimestamp();
    }

    public void resetCacheTimestamp() {
        cacheTimestamp = new SimpleDateFormat("MMddHHmmss").format(new Date());
    }

    protected int handleInSession(ModelImpl model, int handleStatus) throws Exception {
        model.getPathInfo().moveToNextPath(); // skip timestamp in the path
        return super.handleInSession(model, handleStatus);
    }


    public String getResourceUrl(ModelImpl model, String resourceName) {
        try {
            uploadResourceToS3(resourceName);
            StringBuilder resourceUrl = new StringBuilder();
            resourceUrl.append(awsS3BaseUrl);
            resourceUrl.append("/");
            resourceUrl.append(cacheTimestamp);
            resourceUrl.append("/");
            resourceUrl.append(resourceName);
            return resourceUrl.toString();
        } catch (Exception ex) {
            logger.error("failed to upload content to s3",ex);
            return super.getResourceUrl(model, resourceName);
        }
    }

    private void uploadResourceToS3(String resourceName) throws Exception{
        AWSAuthConnection conn =  new AWSAuthConnection(awsAccessKeyId, awsSecretAccessKey);
        S3Object object = new S3Object("this is a test".getBytes(), null);
        Map headers = new TreeMap();
        headers.put("x-amz-acl", Arrays.asList(new String[]{"public-read"}));
        String keyName=  cacheTimestamp+"/"+resourceName;
        String statusText= conn.put(awsBucketName, keyName, object, headers).connection.getResponseMessage();
        if(!"OK".equals(statusText)){
            throw new RuntimeException("upload to s3 failed with status: "+statusText);
        }
    }

    public void setAwsS3BaseUrl(String awsS3BaseUrl) {
        this.awsS3BaseUrl = awsS3BaseUrl;
    }

    public void setAwsBucketName(String awsBucketName) {
        this.awsBucketName = awsBucketName;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) {
        this.awsSecretAccessKey = awsSecretAccessKey;
    }
}
