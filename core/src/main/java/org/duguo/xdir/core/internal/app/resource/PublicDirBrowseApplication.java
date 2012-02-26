package org.duguo.xdir.core.internal.app.resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duguo.xdir.core.internal.app.SimplePathApplication;
import org.duguo.xdir.core.internal.cache.CacheableResponse;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.template.freemarker.StringTemplateUtils;
import org.duguo.xdir.spi.model.GetAndPut;
import org.duguo.xdir.util.datetime.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PublicDirBrowseApplication extends SimplePathApplication {

    private static final Logger logger = LoggerFactory.getLogger(PublicDirBrowseApplication.class);
    private String rootDir;
    private FormatService formatService;
    private GetAndPut service;
    private String dirTemplate;
    
    public void init() throws Exception{

    }

    public int execute(ModelImpl model) throws Exception {
        File requestedFile = buildFile(model);
        if (requestedFile.exists()) {
            disableCache(model);
            setupContentType(model, requestedFile);
            if (requestedFile.isFile()) {
                downloadFile(model,requestedFile);
            } else {
                browseDir(model, requestedFile);
            }
            return STATUS_SUCCESS;
        } else {
            if(logger.isDebugEnabled()) logger.debug("file not found {}", requestedFile.getAbsolutePath());
            return STATUS_PAGE_NOT_FOUND;
        }
    }

    private void setupContentType(ModelImpl model, File requestedFile) {
        String contentType=formatService.resolveContentType(requestedFile.getName());
        model.getResponse().setContentType(contentType);
    }

    private void disableCache(ModelImpl model) {
        if(model.getResponse() instanceof CacheableResponse){
            CacheableResponse cacheableResponse = (CacheableResponse) model.getResponse();
            cacheableResponse.setRequestProcessed();
            model.setResponse(cacheableResponse.getResponse());
        }
    }

    private void browseDir(ModelImpl model, File requestedFile) throws IOException {
        if(logger.isTraceEnabled()) logger.trace("> browseDir {}",requestedFile.getAbsolutePath());
        model.put("app",this);
        Map files=new HashMap();
        model.put("files",files);
        for(File file:requestedFile.listFiles()){
            files.put(file.getName(),new Object[]{file, FileUtils.byteCountToDisplaySize(file.length()), DateTimeUtil.timestampForDisplay(file.lastModified())});
        }

        File indexFile=new File(requestedFile,"index.html");
        String selectedTemplate;
        if(indexFile.exists()){
            selectedTemplate= FileUtils.readFileToString(indexFile);
        }else{
            selectedTemplate=dirTemplate;
        }
        String dirPage= StringTemplateUtils.render(selectedTemplate, model);
        model.getResponse().getWriter().println(dirPage);
        if(logger.isTraceEnabled()) logger.trace("< browseDir");
    }

    private void downloadFile(ModelImpl model,File requestedFile) throws IOException {
        if(logger.isTraceEnabled()) logger.trace("> downloadFile {}",requestedFile.getAbsolutePath());
        InputStream inputStream = new FileInputStream(requestedFile);
        try {
            IOUtils.copy(inputStream, model.getResponse().getOutputStream());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        if(logger.isTraceEnabled()) logger.trace("< downloadFile");
    }

    private File buildFile(ModelImpl model) {
        String remainPath=model.getPathInfo().getRemainPath();
        File requestedFile;
        if(remainPath!=null){
            requestedFile = new File(rootDir, model.getPathInfo().getRemainPath());
        }else{
            requestedFile = new File(rootDir);
        }
        if(logger.isTraceEnabled()) logger.trace("= requested file:" + requestedFile.getAbsolutePath());
        return requestedFile;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public FormatService getFormatService() {
        return formatService;
    }

    public void setFormatService(FormatService formatService) {
        this.formatService = formatService;
    }

    public GetAndPut getService() {
        return service;
    }

    public void setService(GetAndPut service) {
        this.service = service;
    }

    public String getDirTemplate() {
        return dirTemplate;
    }

    public void setDirTemplate(String dirTemplate) {
        this.dirTemplate = dirTemplate;
    }
}
