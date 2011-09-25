package org.duguo.xdir.core.internal.app;


import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;


public class BestPathMatchApplication extends JcrTemplateAwareApplication
{
    
    private static final Logger logger = LoggerFactory.getLogger( BestPathMatchApplication.class );
    
    protected Node resolveNode( ModelImpl model ) throws Exception
    {
        if(logger.isDebugEnabled())
            logger.debug("resolveNode");
        return getJcrFactory().resolveBestNode( model );
    }

    
}
