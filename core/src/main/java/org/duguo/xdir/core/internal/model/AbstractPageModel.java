package org.duguo.xdir.core.internal.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;

import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;


/**
 * All those variable will be lazy loaded
 *
 * @author duguo
 */

public abstract class AbstractPageModel extends AbstractJcrModel {
    private String hostUrl;
    private String virtualHostPath;
    private StringBuilder pageContext;

    private String pagePath;
    private String pageUrl;
    private String pageTitle;
    private List<Object> pagePaths;
    private Map<String, Object> pageProperties;

    private Map<String, Object> pageNavLevel1;
    private Map<String, Object> pageNavLevel2;
    private Map<String, Object> pageNavLevel3;

    private String nodeType;


    /**
     * ****************************************************
     * Modified getter/setter
     * *****************************************************
     */

    /**
     * ****************************************************
     * Public helper methods
     * *****************************************************
     */

    public void addPageNavPath(String newChildTitle, String newChildLinkPath) throws Exception {
        String tempUrl = getPagePath() + "/" + newChildLinkPath + getFormat();
        Map<String, Object> newChildLink = new HashMap<String, Object>();
        newChildLink.put("url", tempUrl);
        newChildLink.put("title", newChildTitle);

        Map<String, Object> childLinks = getPageNavLevel3();
        childLinks.put(newChildLinkPath, newChildLink);
        setPageNavLevel1(getPageNavLevel2());
        setPageNavLevel2(childLinks);
        setPageNavLevel3(new HashMap<String, Object>());
        getPagePaths().add(newChildLink);
        setPageUrl(tempUrl);
        setPageTitle(newChildTitle);
    }

    public String getResourceUrl(String resourceName) {
        return getApp().getResource().getResourceUrl(getModel(), resourceName);
    }

    public Map<String, Object> listChildren(Node currentNode) throws Exception {
        if (isAdvancedView()) {
            return listAllChildren(currentNode);
        } else {
            return listDisplayableChildren(currentNode);
        }
    }

    public Map<String, Object> listAllChildren(Node currentNode) throws Exception {
        NodeIterator nodeIterator = currentNode.getNodes();
        Map<String, Object> childrenMap = new HashMap<String, Object>();
        while (nodeIterator.hasNext()) {
            Node childNode = (Node) nodeIterator.next();
            addTextLinkFromNode(childrenMap, childNode, false);
        }
        return childrenMap;
    }

    public Map<String, Object> listDisplayableChildren(Node currentNode) throws Exception {
        NodeIterator nodeIterator = currentNode.getNodes();
        Map<String, Object> childrenMap = new HashMap<String, Object>();
        while (nodeIterator.hasNext()) {
            Node childNode = (Node) nodeIterator.next();
            addTextLinkFromNode(childrenMap, childNode, true);
        }
        return childrenMap;
    }

    public TextProperty loadChildProperty(String relativePath, String propertyName) throws Exception {
        Node childNode = getNode().getNode(relativePath);
        return new TextProperty((ModelImpl) this, childNode.getProperty(propertyName), propertyName);
    }

    public Map<String, Object> listProperties(Node currentNode) throws Exception {
        PropertyIterator propertyIterator = currentNode.getProperties();
        Map<String, Object> currentProperties = new HashMap<String, Object>();
        while (propertyIterator.hasNext()) {
            Property property = (Property) propertyIterator.next();
            addTextProperty(currentProperties, property, false);
        }
        return currentProperties;
    }

    public Map<String, Object> listDisplayableProperties(Node currentNode) throws Exception {
        PropertyIterator propertyIterator = currentNode.getProperties();
        Map<String, Object> currentProperties = new HashMap<String, Object>();
        while (propertyIterator.hasNext()) {
            Property property = (Property) propertyIterator.next();
            addTextProperty(currentProperties, property, true);
        }
        return currentProperties;
    }

    public TextNode createTextLinkFromNode(Node node, String nodePath) throws Exception {
        String nodeLink = buildNodeLink(node, nodePath);
        TextNode textLink = new TextNode(node, nodeLink, JcrNodeUtils.getNodeTitle(node));
        return textLink;
    }

    public String internalBuildUrl(String jcrPath) {
        int basePathLength = getApp().getJcrBasePath().length();
        StringBuilder urlPath = new StringBuilder();
        urlPath.append(getPageContext());
        urlPath.append("/");
        if (jcrPath.length() > basePathLength) {
            urlPath.append(jcrPath.substring(basePathLength + 1));
        } else {
            urlPath.append(TemplateEngine.TEMPLATE_DEFAULT);
        }
        urlPath.append(getFormat());
        return urlPath.toString();
    }

    /**
     * ****************************************************
     * Lazy init methods
     * *****************************************************
     */
    public String getPageUrl() {
        if (pageUrl == null) {
            pageUrl = buildNodeLink(getNode(), JcrNodeUtils.getNodePath(getNode()));
        }
        return pageUrl;
    }


    public String getPagePath() {
        if (pagePath == null) {
            String pageUrl = getPageUrl();
            pagePath = pageUrl.substring(0, pageUrl.length() - getFormat().length());
            if (pagePath.endsWith("/" + TemplateEngine.TEMPLATE_DEFAULT)) {
                pagePath = pagePath.substring(0, pagePath.length() - TemplateEngine.TEMPLATE_DEFAULT.length() - 1);
            }
        }
        return pagePath;
    }

    public String getPageTitle() {
        if (pageTitle == null) {
            pageTitle = JcrNodeUtils.getNodeTitle(getNode());
        }
        return pageTitle;
    }

    public String getNodeType() {
        if (nodeType == null) {
            nodeType = JcrNodeUtils.getNodeType(getNode());
            if (nodeType == null) {
                nodeType = NULL_STRING;
            }
        }
        return nodeType;
    }

    public List<Object> getPagePaths() throws Exception {
        if (pagePaths == null) {
            pagePaths = new ArrayList<Object>();
            Node currentNode = getNode();
            int basePathLength = getApp().getJcrBasePath().length();
            String currentNodePath = currentNode.getPath();
            while (true) {
                TextNode link = createTextLinkFromNode(currentNode, currentNodePath);
                pagePaths.add(0, link);
                currentNode = currentNode.getParent();
                currentNodePath = currentNode.getPath();
                if (currentNodePath.length() < basePathLength) {
                    break;
                }
            }
        }
        return pagePaths;
    }


    public Map<String, Object> getPageNavLevel1() throws Exception {
        if (pageNavLevel1 == null) {
            pageNavLevel1 = listChildren(getNode().getParent().getParent());
        }
        return pageNavLevel1;
    }

    public Map<String, Object> getPageNavLevel2() throws Exception {
        if (pageNavLevel2 == null) {
            pageNavLevel2 = listChildren(getNode().getParent());
        }
        return pageNavLevel2;
    }

    public Map<String, Object> getPageNavLevel3() throws Exception {
        if (pageNavLevel3 == null) {
            pageNavLevel3 = listChildren(getNode());
        }
        return pageNavLevel3;
    }

    public Map<String, Object> getPageProperties() throws Exception {
        if (pageProperties == null) {
            pageProperties = listProperties(getNode());
        }
        return pageProperties;
    }

    /**
     * ****************************************************
     * Private helper methods
     * *****************************************************
     */


    private Map<String, Object> listSiteChildren(Site site) {
        Map<String, Object> childrenMap = new HashMap<String, Object>();
        if (site.getChildren() != null) {
            for (Site child : site.getChildren().values()) {
                childrenMap.put(child.getName(), child);
            }
        }
        return childrenMap;
    }

    private String buildNodeLink(Node node, String nodePath) {
        String nodeLink = JcrNodeUtils.getNodeUrl(node);
        if (nodeLink == null) {
            nodeLink = internalBuildUrl(nodePath);
        } else {
            nodeLink = getApp().getProps().resolveStringValue(nodeLink);
            nodeLink = PageHelper.displayPropertyValue(nodeLink, (ModelImpl) this);
        }
        return nodeLink;
    }

    private void addTextLinkFromNode(Map<String, Object> links, Node node, boolean displayableOnly) throws Exception {
        String linkPath = node.getPath();
        if (!displayableOnly || JcrNodeUtils.isDisplayable(node.getName())) {
            TextNode link = createTextLinkFromNode(node, linkPath);
            String orderKey = JcrNodeUtils.getOrderKey(node);
            links.put(orderKey, link);
        }
    }

    private void addTextProperty(Map<String, Object> currentProperties, Property property, boolean displayableOnly) throws Exception {
        String propertyName = property.getName();
        if (!displayableOnly || JcrNodeUtils.isDisplayable(propertyName)) {
            TextProperty textProperty = new TextProperty((ModelImpl) this, property, propertyName);
            currentProperties.put(propertyName, textProperty);
        }
    }

    public String internalGetPathFromUrl(String url) {
        return getApp().getJcrBasePath() + url.substring(getPageContext().length());
    }

    /**
     * ****************************************************
     * Setters and getters
     * *****************************************************
     */

    public StringBuilder getPageContext() {
        return pageContext;
    }

    public void setPageContext(StringBuilder pageContext) {
        this.pageContext = pageContext;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setPagePaths(List<Object> pagePaths) {
        this.pagePaths = pagePaths;
    }

    public void setPageProperties(Map<String, Object> pageProperties) {
        this.pageProperties = pageProperties;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        pagePath = null;
    }

    public void setPageNavLevel1(Map<String, Object> pageNavLevel1) {
        this.pageNavLevel1 = pageNavLevel1;
    }

    public void setPageNavLevel2(Map<String, Object> pageNavLevel2) {
        this.pageNavLevel2 = pageNavLevel2;
    }

    public void setPageNavLevel3(Map<String, Object> pageNavLevel3) {
        this.pageNavLevel3 = pageNavLevel3;
    }

    public String getVirtualHostPath() {
        return virtualHostPath;
    }

    public void setVirtualHostPath(String virtualHostPath) {
        this.virtualHostPath = virtualHostPath;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }


}
