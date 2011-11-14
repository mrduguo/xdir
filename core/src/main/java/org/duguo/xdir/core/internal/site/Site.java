package org.duguo.xdir.core.internal.site;

import java.util.List;
import java.util.Map;

import org.duguo.xdir.core.internal.app.Application;

public class Site
{
    private String title;
    private String name;
    private String url;
    private String description;
    private String baseUrl;
    
    private String globalPageTitle;
    private String globalSubTitle;
    private String globalUrl;
    private Site   globalSite;
    private boolean isRoot=false;
    
    private Application app;
    private Site parent;
    private Map<String,Site> children;
    private List<Site> subSites;  // children + meta sites
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle( String title )
    {
        this.title = title;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription( String description )
    {
        this.description = description;
    }
    public Site getParent()
    {
        return parent;
    }
    public void setParent( Site parent )
    {
        this.parent = parent;
    }
    public Map<String, Site> getChildren()
    {
        return children;
    }
    public void setChildren( Map<String, Site> children )
    {
        this.children = children;
    }
    public String getName()
    {
        return name;
    }
    public void setName( String name )
    {
        this.name = name;
    }
    public String getUrl()
    {
        return url;
    }
    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getGlobalPageTitle()
    {
        if(globalSite!=null){
            return globalSite.getGlobalPageTitle();
        }
        return globalPageTitle;
    }
    public void setGlobalPageTitle( String globalPageTitle )
    {
        this.globalPageTitle = globalPageTitle;
    }
    public String getGlobalSubTitle()
    {
        if(globalSite!=null){
            return globalSite.getGlobalSubTitle();
        }
        return globalSubTitle;
    }
    public void setGlobalSubTitle( String globalSubTitle )
    {
        this.globalSubTitle = globalSubTitle;
    }
    public String getGlobalUrl()
    {
        if(globalSite!=null){
            return globalSite.getGlobalUrl();
        }
        return globalUrl;
    }
    public void setGlobalUrl( String globalUrl )
    {
        this.globalUrl = globalUrl;
    }
    public Site getGlobalSite()
    {
        if(globalSite==null){
            return this;
        }else{
            return globalSite;
        }
    }
    public void setGlobalSite( Site globalSite )
    {
        this.globalSite = globalSite;
    }
    public Application getApp()
    {
        return app;
    }
    public void setApp( Application app )
    {
        this.app = app;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public List<Site> getSubSites() {
        return subSites;
    }

    public void setSubSites(List<Site> subSites) {
        this.subSites = subSites;
    }
}
