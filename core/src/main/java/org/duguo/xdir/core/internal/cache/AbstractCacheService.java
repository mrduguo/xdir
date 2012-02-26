package org.duguo.xdir.core.internal.cache;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public abstract class AbstractCacheService {

    public static final String CACHE_INFO = "cache.info";
    public static final String CACHE_CONTENT = "cache.content";
    public final static TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    public final static SimpleDateFormat GMT_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheService.class);
    private String baseDir;

    private File cacheDir;
    private File expiredDir;
    private File responseDir;

    protected void storeAndWriteCache(HttpServletRequest request, CacheableResponse response, WebPageCache webPageCache) throws IOException, FileNotFoundException {
        File cacheFolder = buildCacheFolderFromRequest(request);
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
        File cacheContent = new File(cacheFolder, CACHE_CONTENT);
        if (cacheContent.exists()) {
            cacheContent.delete();
        }

        if (webPageCache.isGzipEnabled()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(webPageCache.getContent());
                outputStream = new GZIPOutputStream(new FileOutputStream(cacheContent));
                IOUtils.copy(inputStream, outputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                FileUtils.deleteQuietly(webPageCache.getContent());
            }
        } else {
            if (!webPageCache.getContent().renameTo(cacheContent)) {
                throw new RuntimeException("failed to put the cache file");
            }
        }


        webPageCache.setContent(cacheContent);
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(cacheFolder, CACHE_INFO)));
            objectOutputStream.writeObject(webPageCache);
            respondWithCache(request, response.getResponse(), webPageCache);
        } finally {
            objectOutputStream.close();
        }
    }

    protected void setupHeaders(CacheableResponse response, WebPageCache webPageCache) {
        setupHeaderLastModified(response, webPageCache);
        setupHeaderGzip(response, webPageCache);
        setupHeaderCacheControl(response);
        String[][] headers = new String[response.getHeaders().size()][2];
        int i = 0;
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            String headerKey = entry.getKey();
            String headerValue = entry.getValue();
            headers[i] = new String[]{headerKey, headerValue};
            i++;
        }
        webPageCache.setHeaders(headers);
    }

    private void setupHeaderCacheControl(CacheableResponse response) {
        String cacheControlHeader=response.getHeaders().get("Cache-Control");
        if(cacheControlHeader!=null){
            if(cacheControlHeader.contains("public")){
                String maxAge=cacheControlHeader.replaceAll("[^0-9]+", "");
                Calendar expireDate=Calendar.getInstance();
                expireDate.add(Calendar.SECOND,Integer.parseInt(maxAge));
                response.setHeader("Expires", GMT_DATE_FORMAT.format(expireDate.getTime()));
            }
        }else{
            response.setHeader("Cache-Control", "must-revalidate");
        }
    }

    private void setupHeaderGzip(CacheableResponse response, WebPageCache webPageCache) {
        if (webPageCache.isGzipEnabled()) {
            response.addHeader("Content-Encoding", "gzip");
        }
    }

    private void setupHeaderLastModified(CacheableResponse response, WebPageCache webPageCache) {
        Calendar calendar = Calendar.getInstance(GMT_TIMEZONE);
        webPageCache.setTimestamp(calendar.getTimeInMillis());
        response.addHeader("Last-Modified", GMT_DATE_FORMAT.format(calendar.getTime()));
    }

    protected HttpServletResponse verifyRoleAndWriteCache(HttpServletRequest request, HttpServletResponse response, File cacheInfo) {
        try {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(new FileInputStream(cacheInfo));
                WebPageCache webPageCache = (WebPageCache) objectInputStream.readObject();
                objectInputStream.close();
                objectInputStream = null;
                if (!isModifiedSince(request, response, webPageCache.getTimestamp())) {
                    return null;
                } else {
                    respondWithCache(request, response, webPageCache);
                    return null;
                }
            } finally {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to read cache: " + ex.getMessage(), ex);
        }
        return response;
    }

    private boolean isModifiedSince(HttpServletRequest request, HttpServletResponse response, long timestamp) {
        try {
            String clientTimeStr = request.getHeader("If-Modified-Since");
            if (clientTimeStr != null) {
                if (timestamp / 1000 == GMT_DATE_FORMAT.parse(clientTimeStr).getTime() / 1000) {
                    if(logger.isTraceEnabled())  logger.trace("= isModifiedSince return 304");
                    response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                    return false;
                }
            }
        } catch (Exception ex) {
            logger.error("failed to process If-Modified-Since" + ex.getMessage(), ex);
        }
        return true;
    }

    protected HttpServletResponse createCachableResponse(HttpServletResponse response, File cacheFolder) throws IOException {
        WebPageCache webPageCache = new WebPageCache();
        webPageCache.setContent(responseDir);
        return new CacheableResponse(response, webPageCache);
    }

    protected void expirePath(String cachePath) {
        File cacheFolder = new File(cacheDir, cachePath);
        if (cacheFolder.exists()) {
            if(!expiredDir.exists()){
                expiredDir.mkdirs();
            }
            File expiredFile = new File(expiredDir, String.valueOf(System.currentTimeMillis()));
            if (!cacheFolder.renameTo(expiredFile)) {
                logger.warn("Failed to expire cache: ", cacheFolder.getPath());
                deleteDir(cacheFolder);
            }
        }
    }

    protected File buildCacheFolderFromRequest(HttpServletRequest request) {
        return getCacheFolderFromUrl(request.getRequestURL().toString());
    }

    protected File getCacheFolderFromUrl(String url) {
        File cacheFolder = new File(cacheDir, url.replaceAll(":", "/"));
        return cacheFolder;
    }
    
    public void initCache(String url) {
        File cacheFolder=getCacheFolderFromUrl(url);
        if(!cacheFolder.exists()){
            cacheFolder.mkdirs();
        }
    }

    public boolean isCacheExists(String url) {
        return getCacheFolderFromUrl(url).exists();
    }

    protected void respondWithCache(HttpServletRequest request, HttpServletResponse response, WebPageCache cache) throws IOException {
        if(logger.isTraceEnabled())  logger.trace("> respondWithCache");
        if (cache.getContentType() != null) {
            response.setContentType(cache.getContentType());
        }
        if (isGzipSourceForNoneGzipRequest(request, cache)) {
            writeHeaderAndDecodedAsNoneGzipStream(response, cache);
        } else {
            writeHeaderAndBinaryStream(response, cache);
        }
        if(logger.isTraceEnabled())  logger.trace("< respondWithCache");
    }

    private void writeHeaderAndBinaryStream(HttpServletResponse response, WebPageCache cache) throws IOException {
        if (cache.getHeaders() != null) {
            for (String[] header : cache.getHeaders()) {
                response.addHeader(header[0], header[1]);
            }
        }
        response.addHeader("Content-Length",String.valueOf(cache.getContent().length()));
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(cache.getContent());
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } finally {
            fileInputStream.close();
        }
    }

    private void writeHeaderAndDecodedAsNoneGzipStream(HttpServletResponse response, WebPageCache cache) throws IOException {
        if(logger.isDebugEnabled())  logger.debug("respond with plain text for gzip content");
        if (cache.getHeaders() != null) {
            for (String[] header : cache.getHeaders()) {
                if (!header[0].equals("Content-Encoding")) {
                    response.addHeader(header[0], header[1]);
                }
            }
        }
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(cache.getContent());
            fileInputStream = new GZIPInputStream(fileInputStream);
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } finally {
            fileInputStream.close();
        }
    }

    private boolean isGzipSourceForNoneGzipRequest(HttpServletRequest request, WebPageCache cache) {
        if (cache.isGzipEnabled()) {
            String acceptEncodingHeader = request.getHeader("Accept-Encoding");
            if (acceptEncodingHeader == null || !acceptEncodingHeader.contains("gzip")) {
                return true;
            }
        }
        return false;
    }


    protected String buildVersionStamp() {
        // example ouput yMdhmss
        // example ouput 91122933
        StringBuffer versionSB = new StringBuffer();
        Calendar now = Calendar.getInstance();
        versionSB.append((now.get(Calendar.YEAR) % 10));
        versionSB.append(now.get(Calendar.MONTH) + 1);
        versionSB.append(now.get(Calendar.DAY_OF_MONTH));
        versionSB.append(now.get(Calendar.HOUR_OF_DAY));
        versionSB.append(now.get(Calendar.MINUTE));
        versionSB.append(now.get(Calendar.SECOND));
        return versionSB.toString();
    }

    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File file : children) {
                deleteDir(file);
            }
        }
        if (!dir.delete()) {
            logger.warn("failed to delete {}", dir.getPath());
        }
    }

    protected void initDir() {
        File cacheRootFile = new File(baseDir);
        if (cacheRootFile.exists()) {
            deleteDir(cacheRootFile);
        }

        cacheDir = new File(cacheRootFile, "root");
        responseDir = new File(cacheRootFile, "response");
        expiredDir = new File(cacheRootFile, "expired");
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
