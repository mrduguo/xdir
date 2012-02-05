package org.duguo.xdir.jcr.utils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JcrNodeUtils {
    private static final Logger logger = LoggerFactory.getLogger(JcrNodeUtils.class);

    public static final String JCR_NODE_TITLE = "_title";
    public static final String JCR_NODE_TYPE = "_type";
    public static final String JCR_NODE_ORDERKEY = "_order";
    public static final String JCR_NODE_URL = "_url";
    public static final String JCR_NODE_DESCRIPTION = "_description";
    public static final String JCR_NODE_SEARCH = "_search";
    public static final String JCR_PREFIX = "jcr:";

    public static final SimpleDateFormat JCR_TIMESTAMP1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final SimpleDateFormat JCR_TIMESTAMP2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final String JCR_VERSIONING_NAME = "jcr:versioning";
    public static final Set<String> PROTECTED_PROPERTIES = new HashSet<String>();


    static {
        PROTECTED_PROPERTIES.add("jcr:primaryType");
        PROTECTED_PROPERTIES.add("jcr:baseVersion");
        PROTECTED_PROPERTIES.add("jcr:isCheckedOut");
        PROTECTED_PROPERTIES.add("jcr:mixinTypes");
        PROTECTED_PROPERTIES.add("jcr:predecessors");
        PROTECTED_PROPERTIES.add("jcr:uuid");
        PROTECTED_PROPERTIES.add("jcr:versionHistory");
    }


    public static boolean isDisplayable(String relPath) {
        return relPath.charAt(0) != '_' && !relPath.startsWith(JCR_PREFIX);
    }


    public static String getNodePath(Node node) {
        try {
            return node.getPath();
        } catch (Exception ex) {
            logger.error("failed to retrive node path", ex);
        }
        return null;
    }


    public static String getNodeTitle(Node node) {
        String title = getPropertyIfExist(node, JCR_NODE_TITLE);
        try {
            if (title == null) {
                title = node.getName();
                title = getDisplayablePropertyName(title);
            }
        } catch (Exception ex) {
            logger.error("failed to retrive title", ex);
        }
        return title;
    }


    public static String getOrderKey(Node node) {
        String orderKey = getPropertyIfExist(node, JCR_NODE_ORDERKEY);
        try {
            if (orderKey == null) {
                orderKey = node.getName();
            }
        } catch (Exception ex) {
            logger.error("failed to retrive order key", ex);
        }
        return orderKey;
    }


    public static String getNodeDescription(Node node) {
        return getPropertyIfExist(node, JCR_NODE_DESCRIPTION);
    }


    public static String getNodeUrl(Node node) {
        return getPropertyIfExist(node, JCR_NODE_URL);
    }


    public static String getNodeType(Node node) {
        return getPropertyIfExist(node, JCR_NODE_TYPE);
    }


    public static String getNodeTypeName(Node node) throws Exception {
        String nodeType = getNodeType(node);
        if (nodeType != null) {
            nodeType = getDisplayablePropertyName(nodeType);
        }
        return nodeType;
    }


    public static String getPropertyIfExist(Node node, String propertyName) {
        return getPropertyIfExist(node, propertyName, null);
    }


    public static String getPropertyIfExist(Node node, String propertyName, String defaultValue) {
        try {
            if (node.hasProperty(propertyName)) {
                return getPropertyStringValue(node.getProperty(propertyName));
            }
        } catch (Exception ex) {
            logger.error("failed to retrive property", ex);
        }
        return defaultValue;
    }


    public static Node loadOrCreatePath(Node parentNode, String paths) throws Exception {
        for (String path : paths.split("/")) {
            if (path.length() > 0) {
                if (parentNode.hasNode(path)) {
                    parentNode = parentNode.getNode(path);
                } else {
                    parentNode = parentNode.addNode(path, "nt:xunstructured");
                    if (logger.isDebugEnabled())
                        logger.debug("node {} created", parentNode.getPath());
                }
            }
        }
        return parentNode;
    }


    public static String getStringProperty(Node node, String relPath) {
        try {
            if (node.hasProperty(relPath)) {
                return getPropertyStringValue(node.getProperty(relPath));
            }
        } catch (Exception ex) {
            logger.error("failed to retrive string property", ex);
        }
        return null;
    }


    public static String getPropertyStringValue(Property property) {
        try {
            try {
                return property.getString();
            } catch (ValueFormatException ex) {
                String textProperty = null;
                for (Value value : property.getValues()) {
                    if (textProperty != null) {
                        textProperty += ",";
                    }
                    textProperty += value.getString();
                }
                if (textProperty == null) {
                    textProperty = "";
                }
                return textProperty;
            }
        } catch (Exception ex) {
            logger.error("failed to retrive string property", ex);
        }
        return null;
    }


    public static String getDisplayablePropertyName(String rawName) {
        String[] allparts = rawName.split("_");
        StringBuffer newName = null;
        for (String str : allparts) {
            if (newName == null) {
                newName = new StringBuffer();
            } else {
                newName.append(" ");
            }
            if (str.length() > 0) {
                newName.append(str.substring(0, 1).toUpperCase());
                if (str.length() > 1) {
                    newName.append(str.substring(1));
                }
            }
        }
        return newName.toString();
    }


    public static boolean isRootNode(Node currentNode) {
        try {
            return currentNode.getPath().length() == 1;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public static String retrivePropertyIncludeParent(Node currentNode, String propertyName) {
        String currentValue = JcrNodeUtils.getPropertyIfExist(currentNode, propertyName);
        if (currentValue == null && !JcrNodeUtils.isRootNode(currentNode)) {
            try {
                currentValue = retrivePropertyIncludeParent(currentNode.getParent(), propertyName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return currentValue;
    }


    public static String retriveRelativePathProperty(Node currentNode, String propertyName) {
        String currentPath = JcrNodeUtils.getPropertyIfExist(currentNode, propertyName);
        if (currentPath == null && !JcrNodeUtils.isRootNode(currentNode)) {
            try {
                currentPath = retriveRelativePathProperty(currentNode.getParent(), propertyName);
                if (currentPath != null) {
                    currentPath = currentPath + "/" + currentNode.getName();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return currentPath;
    }


    public static void setNodeProperty(Node node, String path, String value) throws Exception {
        if (path.indexOf("/") > 0) {
            String tempPath = path.substring(0, path.lastIndexOf("/"));
            node = loadOrCreatePath(node, tempPath);
            path = path.substring(path.lastIndexOf("/") + 1);
        }
        if (value != null && value.length() > 0) {
            if (path.equals("jcr:data")) {
                ByteArrayInputStream dataInputStream = new ByteArrayInputStream(value.getBytes());
                setStreamProperty(node, dataInputStream);
                if (logger.isDebugEnabled())  logger.debug("created binary property [{}]", path);
            } else {
                int valueLength=value.length();
                if (valueLength==24 && valueLength==29 && value.charAt(10)=='T' && value.charAt(4)=='-') {
                    Calendar targetTime = Calendar.getInstance();
                    try {
                        if(value.length()==24){
                            targetTime.setTime(JCR_TIMESTAMP2.parse(value));
                        }else{
                            targetTime.setTime(JCR_TIMESTAMP1.parse(value));
                        }
                        node.setProperty(path, targetTime);
                        if(logger.isTraceEnabled())logger.trace("set date property success:"+value);
                    } catch (ParseException notAValidDate) {
                        if(logger.isTraceEnabled())logger.trace("set date property failed:"+value);
                        node.setProperty(path, value);
                    }
                } else {
                    node.setProperty(path, value);
                }
            }
        } else {
            node.setProperty(path, (Value) null);
        }
    }


    public static Node addFileNode(Node parentNode, String nodePath, File file) throws Exception {
        Node fileNode = JcrNodeUtils.loadOrCreatePath(parentNode, nodePath);
        fileNode.setPrimaryType("nt:xfile");
        Node contentNode = null;
        if (fileNode.hasNode("jcr:content")) {
            contentNode = fileNode.getNode("jcr:content");
        } else {
            contentNode = fileNode.addNode("jcr:content");//, "nt:resource" );
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            JcrNodeUtils.setStreamProperty(contentNode, fileInputStream);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return fileNode;
    }


    public static void setStreamProperty(Node node, InputStream inputStream) throws Exception {
        Binary binary = node.getSession().getValueFactory().createBinary(inputStream);
        node.setProperty("jcr:data", binary);
    }

}
