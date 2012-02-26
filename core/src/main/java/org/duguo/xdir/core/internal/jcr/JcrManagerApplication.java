package org.duguo.xdir.core.internal.jcr;


import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.TextNode;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.duguo.xdir.spi.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.HashMap;
import java.util.Map;

public class JcrManagerApplication extends JcrTemplateAwareApplication {
    private static final Logger logger = LoggerFactory.getLogger(JcrManagerApplication.class);

    protected int handleInSession(ModelImpl model, int handleStatus) throws Exception {
        getFormat().resolveFormat(model);
        setupAction(model);
        Node templateNode = model.getSession().getNode(model.getApp().getJcrBasePath());
        model.setNode(templateNode);
        return handleWithTargetJcrNode(model, handleStatus);
    }

    protected int handleWithTargetJcrNode(ModelImpl model, int handleStatus) throws Exception {
        String jcrPath = null;
        if (model.getPathInfo().getCurrentPath() != null) {
            jcrPath = model.getPathInfo().getRemainPath();
        } else {
            jcrPath = "/";
        }
        if (logger.isDebugEnabled())
            logger.debug("jcr path [{}]", jcrPath);
        Node jcrNode = null;
        if (model.getSession().itemExists(jcrPath)) {
            jcrNode = model.getSession().getNode(jcrPath);
        } else if (model.getSession().itemExists(jcrPath + model.getFormat())) {
            jcrNode = model.getSession().getNode(jcrPath + model.getFormat());
            model.setFormat(FormatService.FORMAT_FOLDER);
        } else {
            return STATUS_PAGE_NOT_FOUND;
        }
        if (jcrNode != null) {
            setupTargetJcrNodeNavLinks(model, jcrNode);
            if (logger.isDebugEnabled())
                logger.debug("jcr browser handle site [{}]", model.getPathInfo().getRemainPath());
            model.setNodeType(Model.NULL_STRING);
            handleStatus = getTemplate().process(model, TemplateEngine.TEMPLATE_DEFAULT, model.getNodeType());
        }
        return handleStatus;
    }


    protected void setupTargetJcrNodeNavLinks(ModelImpl model, Node jcrNode) throws Exception {
        String jcrBasePath = model.getPageContext().toString();

        if (JcrNodeUtils.isRootNode(jcrNode)) {
            model.getPageNavLevel1();
            model.getPageNavLevel2();

            model.getPagePaths();
            model.getPageUrl();
            model.getPageTitle();
        } else {
            if (JcrNodeUtils.isRootNode(jcrNode.getParent())) {
                model.setPageNavLevel1(model.getPageNavLevel2());
                model.setPageNavLevel2(listChildren(model, jcrNode.getParent(), jcrBasePath));
            } else {
                model.setPageNavLevel1(listChildren(model, jcrNode.getParent().getParent(), jcrBasePath));
                model.setPageNavLevel2(listChildren(model, jcrNode.getParent(), jcrBasePath));
            }
            model.setPageUrl(jcrBasePath + jcrNode.getPath() + model.getFormat());
            model.setPageTitle(jcrNode.getName());
            addNodeParentsToPaths(model, jcrNode, jcrBasePath);
        }
        model.setPageNavLevel3(listChildren(model, jcrNode, jcrBasePath));
        model.setNode(jcrNode);
    }


    protected Map<String, Object> listChildren(ModelImpl model, Node currentNode, String jcrBasePath)
            throws Exception {
        NodeIterator nodeIterator = currentNode.getNodes();
        Map<String, Object> childrenMap = new HashMap<String, Object>();
        int index = 0;
        while (nodeIterator.hasNext()) {
            Node childNode = (Node) nodeIterator.next();
            String nodeName = childNode.getName();
            String nodeLink = jcrBasePath + childNode.getPath() + model.getFormat();
            TextNode textLink = new TextNode(childNode, nodeLink, nodeName);
            childrenMap.put(nodeName + index, textLink);
            index++;
        }
        return childrenMap;
    }


    protected void addNodeParentsToPaths(ModelImpl model, Node jcrNode, String jcrBasePath) throws Exception {
        if (!JcrNodeUtils.isRootNode(jcrNode)) {
            addNodeParentsToPaths(model, jcrNode.getParent(), jcrBasePath);
            addPathsLink(model, jcrNode, jcrBasePath + jcrNode.getPath() + model.getFormat(), jcrNode.getName());
        }
    }


    private void addPathsLink(ModelImpl model, Node jcrNode, String linkUrl, String title) throws Exception {
        TextNode textLink = new TextNode(jcrNode, linkUrl, title);
        model.getPagePaths().add(textLink);
    }

}
