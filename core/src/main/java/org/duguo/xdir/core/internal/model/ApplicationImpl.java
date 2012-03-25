package org.duguo.xdir.core.internal.model;

import org.duguo.xdir.core.internal.app.resource.ResourceService;
import org.duguo.xdir.core.internal.config.PropertiesService;
import org.duguo.xdir.core.internal.jcr.JcrService;
import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.duguo.xdir.spi.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ApplicationImpl extends AbstractGetAndPut
{
    private static final Logger logger = LoggerFactory.getLogger( ApplicationImpl.class );
    private String basePath;
    private String baseUrl;
    private String[] parents;
    private Map<String, String[]> formats;
    private Site site;
    
    private ResourceService resource;
    private JcrService jcr;
    private PropertiesService props;


    /*************************************************
     * helper methods
     *************************************************/

    public String[] getFormatList( String format )
    {
        return formats.get( format );
    }


    /*************************************************
     * setter methods
     *************************************************/

    public void setBasePath( String rawBasePath )
    {
        setupBasePath( rawBasePath );
    }


    public void setBaseUrl( String rawBaseUrl )
    {
        setupBaseUrl( rawBaseUrl );
    }


    public void setParents( String[] parents )
    {
        this.parents=parents;
    }


    public void setFormats( Map<String, String[]> formats )
    {
        this.formats=formats;
    }


    /*************************************************
     * default getter and setter methods
     *************************************************/

    public String getBaseUrl()
    {
        return baseUrl;
    }


    public String[] getParents()
    {
        return parents;
    }


    public Map<String, String[]> getFormats()
    {
        return formats;
    }


    public String getBasePath()
    {
        return basePath;
    }


    public ResourceService getResource()
    {
        return resource;
    }


    public void setResource( ResourceService resource )
    {
        this.resource = resource;
    }



    /*************************************************
     * setup methods
     *************************************************/


    private void setupBasePath( String rawBasePath )
    {
        basePath = null;
        if ( rawBasePath != null )
        {
            int prefixIndex = rawBasePath.indexOf( '/' );
            if ( prefixIndex > 0 )
            {
                basePath = rawBasePath.substring( prefixIndex );
            }
        }
        if ( basePath == null )
        {
            basePath = rawBasePath;
        }
        if ( basePath != rawBasePath && logger.isDebugEnabled() )
            logger.debug( "normalized base path [{}] to [{}]", rawBasePath, basePath );
    }


    private void setupBaseUrl( String rawBaseUrl )
    {
        if ( rawBaseUrl != null )
        {
            baseUrl = HttpUtil.normalizeUrl( rawBaseUrl );
            if ( baseUrl != rawBaseUrl && logger.isDebugEnabled() )
                logger.debug( "normalized base url [{}] to [{}]", rawBaseUrl, baseUrl );
        }
        else
        {
            baseUrl = null;
        }
    }


    public Site getSite()
    {
        return site;
    }


    public void setSite( Site site )
    {
        this.site = site;
    }


    public JcrService getJcr()
    {
        return jcr;
    }


    public void setJcr( JcrService jcr )
    {
        this.jcr = jcr;
    }


    public PropertiesService getProps()
    {
        return props;
    }


    public void setProps( PropertiesService props )
    {
        this.props = props;
    }

}
