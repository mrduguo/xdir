package org.duguo.xdir.core.internal.template.freemarker;

import freemarker.core.Environment;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * Remove the predefined marcro form the context


<#assign removenamespace = "org.duguo.xdir.core.internal.template.freemarker.RemoveNamespaceDirective"?new()>
<@removenamespace namespace="action_content"/>


 */

public class RemoveNamespaceDirective  implements TemplateDirectiveModel{
    
    
    private static final Logger logger = LoggerFactory.getLogger( RemoveNamespaceDirective.class );

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
	    Object rawNamespace=params.get( "namespace" );
        Assert.notNull( rawNamespace, "namespace is required" );
        
	    String namespaceToBeRemoved=((SimpleScalar)rawNamespace).getAsString();
        env.getCurrentNamespace().remove( namespaceToBeRemoved );
        env.getCurrentNamespace().remove( "removenamespace" );
        
        if(logger.isDebugEnabled())
            logger.debug("namespace [{}] removed",namespaceToBeRemoved);
	}
}
