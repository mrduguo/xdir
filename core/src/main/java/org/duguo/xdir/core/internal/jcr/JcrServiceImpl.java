package org.duguo.xdir.core.internal.jcr;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duguo.xdir.core.internal.exception.XdirException;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.utils.RequestUtils;
import org.duguo.xdir.jcr.utils.JcrImportUtils;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.duguo.xdir.jcr.utils.Utf8SortedProperties;
import org.duguo.xdir.spi.service.DynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JcrServiceImpl extends AbstractUpdateJcrService implements JcrService,DynamicService {
    private static final Logger logger = LoggerFactory.getLogger(JcrServiceImpl.class);

    public void download(ModelImpl model) throws Exception {
        if (logger.isTraceEnabled()) logger.trace("> download {}", model.getNode().getPath());
        String format = resolveDownloadFormat(model);
        setupDownloadHeader(model, format);

        OutputStream outputStream = model.getResponse().getOutputStream();
        try {
            if (format.equals("xml")) {
                model.getSession().exportSystemView(model.getNode().getPath(), outputStream, false, false);
            } else {
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
                outputStream = zipOutputStream;
                int parentPathLength=model.getNode().getParent().getPath().length();
                if(parentPathLength>1){
                    parentPathLength=parentPathLength+1;
                }
                addNodeToStreamRecursively(parentPathLength, model.getNode(), zipOutputStream);
            }
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        if (logger.isTraceEnabled()) logger.trace("< download");
    }


    public void importFile(ModelImpl model) throws Exception {
        String pathName = RequestUtils.getPath(model);
        String originalFileName;
        File localFile;
        if (pathName != null) {
            localFile = new File(pathName);
            originalFileName = localFile.getName();
        } else {
            localFile = (File) model.getRequest().getParameterMap().get("uploadedfile");
            originalFileName = (String) model.getRequest().getParameterMap().get("uploadedfilefileName");
        }
        if (!localFile.exists()) {
            model.setStatus("file " + localFile.getAbsolutePath() + " doesn't exist");
            return;
        }

        String tempNodeName="tmp-"+System.currentTimeMillis();
        Node tmpNode=model.getNode().addNode(tempNodeName);
        if (originalFileName.endsWith("-jcr.xml")) {
            importJcrXmlFile(model,tmpNode, localFile);
        } else {
            importFolderOrZipFile(model,tmpNode, originalFileName, localFile);
        }
        
        Node nodeToBeImported=(Node)tmpNode.getNodes().next();
        if(model.getNode().hasNode(nodeToBeImported.getName())){
            model.getNode().getNode(nodeToBeImported.getName()).remove();
        }
        model.getSession().save();
        model.getSession().move(nodeToBeImported.getPath(), model.getNode().getPath()+"/"+nodeToBeImported.getName());
        tmpNode.remove();
        saveNode(model);
    }

    private void importFolderOrZipFile(ModelImpl model,Node parentNode, String originalFileName, File jcrFolder) throws Exception {
        File unzipTargetFolder = null;
        try {
            if (originalFileName.endsWith("-jcr.zip")) {
                unzipTargetFolder = new File(jcrFolder.getAbsolutePath()+".tmp");
                ZipFile zipFile = new ZipFile(jcrFolder);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    File targetFile = new File(unzipTargetFolder, entry.getName());
                    if (entry.isDirectory()) {
                        if (logger.isTraceEnabled()) logger.trace("extracting zip folder {}", entry.getName());
                        targetFile.mkdirs();
                        continue;
                    }
                    if (logger.isTraceEnabled()) logger.trace("extracting zip file {}", entry.getName());
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    IOUtils.copy(zipFile.getInputStream(entry), new FileOutputStream(targetFile));
                }
                zipFile.close();
                jcrFolder.delete();
                jcrFolder =unzipTargetFolder;
                if (logger.isDebugEnabled()) logger.debug("extracted zip files to {}", jcrFolder.getAbsolutePath());
            }
            JcrImportUtils.importFolder(parentNode, jcrFolder);
            if (logger.isDebugEnabled()) logger.debug("imported files to {}", model.getNode().getPath());
        } finally {
            FileUtils.deleteQuietly(unzipTargetFolder);
        }
    }

    private void importJcrXmlFile(ModelImpl model,Node parentNode, File jcrXmlFile) throws IOException, RepositoryException {
        FileInputStream fileInputStream = new FileInputStream(jcrXmlFile);
        try {
            model.getSession().importXML(parentNode.getPath(), fileInputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private void setupDownloadHeader(ModelImpl model, String format) {
        if ("download".equals(model.getRequest().getParameter("action"))) {
            String fileName = model.getPageTitle() + "-" + new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            model.getResponse().setContentType("application/x-download;charset=UTF-8");
            String downloadFileName = fileName + "-jcr." + format;
            model.getResponse().setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
            if (logger.isTraceEnabled()) logger.trace("setupDownloadHeader with file name {}", downloadFileName);
        }
    }

    private String resolveDownloadFormat(ModelImpl model) {
        String format = model.getRequest().getParameter("format");
        if (format == null) {
            format = "zip";
        }
        if (!format.equals("zip") && !format.equals("xml")) {
            throw new XdirException("format " + format + " is not supported");
        }
        if (logger.isTraceEnabled()) logger.trace("resolveDownloadFormat {}", format);
        return format;
    }

    private void addNodeToStreamRecursively(int baseNodePathLength, Node node, ZipOutputStream zipOutputStream) throws Exception {
        String entryPath = node.getPath().substring(baseNodePathLength);
        if (entryPath.indexOf(":") > 0) {
            logger.warn("ignore illegal file path: {}", node.getPath());
            return;
        }
        InputStream inputStream;
        if (node.isNodeType("nt:xfile")) {
            if (logger.isTraceEnabled()) logger.trace("addNodeToStreamRecursively with file {}", entryPath);
            zipOutputStream.putNextEntry(new ZipEntry(entryPath));
            inputStream = node.getNode("jcr:content").getProperty("jcr:data").getBinary().getStream();
            try {
                IOUtils.copy(inputStream, zipOutputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            zipOutputStream.closeEntry();
            return;
        } else {
            Utf8SortedProperties _properties = new Utf8SortedProperties();
            PropertyIterator propertyIterator = node.getProperties();
            while (propertyIterator.hasNext()) {
                Property property = (Property) propertyIterator.next();
                String propertyStringValue = JcrNodeUtils.getPropertyStringValue(property);
                if(propertyStringValue.indexOf("\n")>=0){
                    String singlePropertyPath = entryPath + "/"+JcrImportUtils.PROPERTIES_FILE_PREFIX+property.getName();
                    if (logger.isTraceEnabled()) logger.trace("add multi line string properties as file {}", singlePropertyPath);
                    zipOutputStream.putNextEntry(new ZipEntry(singlePropertyPath));
                    IOUtils.write(propertyStringValue,zipOutputStream);
                    zipOutputStream.closeEntry();
                }else{
                    _properties.put(property.getName(), propertyStringValue);
                }
            }
            if (_properties.get("jcr:primaryType").contains("unstructured")) {
                _properties.remove("jcr:primaryType");
            }
            if (_properties.size() > 0) {
                entryPath = entryPath + "/"+JcrImportUtils.PROPERTIES_FILE_PREFIX;
                if (logger.isTraceEnabled()) logger.trace("addNodeToStreamRecursively with node {}", entryPath);
                zipOutputStream.putNextEntry(new ZipEntry(entryPath));
                _properties.store(zipOutputStream);
                zipOutputStream.closeEntry();
            } else {
                entryPath = entryPath + "/";
                if (logger.isTraceEnabled()) logger.trace("addNodeToStreamRecursively with folder {}", entryPath);
                zipOutputStream.putNextEntry(new ZipEntry(entryPath));
                zipOutputStream.closeEntry();
            }
        }
        NodeIterator children = node.getNodes();
        while (children.hasNext()) {
            addNodeToStreamRecursively(baseNodePathLength, (Node) children.next(), zipOutputStream);
        }
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.jcr.service.name","jcr");
    }
}
