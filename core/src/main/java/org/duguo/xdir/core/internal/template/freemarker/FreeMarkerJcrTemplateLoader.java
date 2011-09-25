package org.duguo.xdir.core.internal.template.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.exception.ResourceNotFoundException;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.ResourceLoader;

import freemarker.cache.TemplateLoader;

public class FreeMarkerJcrTemplateLoader implements TemplateLoader {
	
    
    private static final Logger logger = LoggerFactory.getLogger( FreeMarkerJcrTemplateLoader.class );
    
    private ResourceLoader templateStringLoader;
    private ModelImpl model;
    private String nodeType;

	public FreeMarkerJcrTemplateLoader(ResourceLoader templateStringLoader,ModelImpl model,String nodeType) {
	    this.templateStringLoader=templateStringLoader;
	    this.model=model;
	    this.nodeType=nodeType;
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
	}

	public Object findTemplateSource(String name) throws IOException {
		return name;
	}

	public long getLastModified(Object templateSource) {
		return 0;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
	    String templateName=(String)templateSource;
        String templateString=templateStringLoader.load( model, templateName ,nodeType);
        if(templateString==null){
            logger.warn("template [{}] not found",templateName);
            throw new ResourceNotFoundException( templateName );
        }
		return new StringReader(templateString);
	}

}
