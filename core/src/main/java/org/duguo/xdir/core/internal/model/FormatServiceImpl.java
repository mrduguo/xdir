package org.duguo.xdir.core.internal.model;

import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class FormatServiceImpl implements FormatService {
    private static final Logger logger = LoggerFactory.getLogger(FormatServiceImpl.class);
    private Map<String, String> formatMatchers;

    String defaultContentType;
    Map<String, String> textContentTypes;
    Map<String, String> binaryContentTypes;

    public void resolveFormat(ModelImpl model) throws IOException {
        String requestUri = model.getRequest().getRequestURI();
        if (logger.isTraceEnabled()) logger.trace("> resolveFormat {}", requestUri);
        String format = null;
        if (requestUri.charAt(requestUri.length() - 1) == '/') {
            // e.g. /admin/
            format = FORMAT_FOLDER;
            model.setFormat(format);

        } else {
            int dotPosition = -1;
            String[] paths = model.getPathInfo().getPaths();
            if (paths.length > 0) {
                format = paths[paths.length - 1];
                dotPosition = format.lastIndexOf('.');
            }
            if (dotPosition < 0) {
                // e.g. /xdir/apps/www/blog
                format = FORMAT_FOLDER;
                model.setFormat(format);
            } else {
                String pathName = format.substring(0, dotPosition);
                if (TemplateEngine.TEMPLATE_DEFAULT.equals(pathName)) {
                    //e.g. /xdir/apps/www/blog/index.html
                    String[] newPaths = new String[paths.length - 1];
                    for (int i = 0; i < newPaths.length; i++) {
                        newPaths[i] = paths[i];
                    }
                    model.getPathInfo().setPaths(newPaths);
                } else {
                    // e.g. /xdir/apps/www/blog.html
                    paths[paths.length - 1] = pathName;
                }
                format = format.substring(dotPosition);
                model.setFormat(format);
            }
        }
        if (logger.isTraceEnabled()) logger.trace("< resolveFormat {}", format);
    }

    public String autoDetectFormat(ModelImpl model) {
        String format = null;
        String useragent = model.getRequest().getHeader("User-Agent");
        if (useragent != null && formatMatchers != null) {
            for (Map.Entry<String, String> entry : formatMatchers.entrySet()) {
                if (useragent.matches(entry.getValue())) {
                    format = entry.getKey();
                    break;
                }
            }
        }
        if (format == null) {
            format = TemplateEngine.FORMAT_DEFAULT;
        }
        return format;
    }

    public String resolveContentType(String fileName) {
        String fileFormat = parseFileFormat(fileName);
        String contentType = null;
        if (textContentTypes.containsKey(fileFormat)) {
            contentType = textContentTypes.get(fileFormat);
        } else if (binaryContentTypes.containsKey(fileFormat)) {
            contentType = binaryContentTypes.get(fileFormat);
        } else {
            contentType = defaultContentType;
            logger.warn("unknown file format for file [{}]", fileName);
        }
        return contentType;
    }

    public boolean isText(String fileName) {
        String fileFormat = parseFileFormat(fileName);
        return textContentTypes.containsKey(fileFormat);
    }

    public boolean isBinary(String fileName) {
        String fileFormat = parseFileFormat(fileName);
        return binaryContentTypes.containsKey(fileFormat);
    }

    protected String parseFileFormat(String fileName) {
        String fileType = null;
        int splitIndex = fileName.lastIndexOf(".");
        if (splitIndex > 0) {
            fileType = fileName.substring(splitIndex);
        } else {
            fileType = fileName;
        }
        return fileType;
    }


    public void setFormatMatchers(Map<String, String> formatMatchers) {
        this.formatMatchers = formatMatchers;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }

    public void setDefaultContentType(String defaultContentType) {
        this.defaultContentType = defaultContentType;
    }

    public Map<String, String> getTextContentTypes() {
        return textContentTypes;
    }

    public void setTextContentTypes(Map<String, String> textContentTypes) {
        this.textContentTypes = textContentTypes;
    }

    public Map<String, String> getBinaryContentTypes() {
        return binaryContentTypes;
    }

    public void setBinaryContentTypes(Map<String, String> binaryContentTypes) {
        this.binaryContentTypes = binaryContentTypes;
    }

    public Map<String, String> getFormatMatchers() {
        return formatMatchers;
    }
}
