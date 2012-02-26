package org.duguo.xdir.core.internal.template.freemarker;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

import org.duguo.xdir.spi.model.GetAndPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.json.Json;


public class GroovyPropertyAccessSupport{
	
    private static final Logger logger = LoggerFactory.getLogger( GroovyPropertyAccessSupport.class );

	@SuppressWarnings("unchecked")
	public void setSupportedClasses(Class[] supportedClasses){
		for(Class supportedClass:supportedClasses){
			setMetaClass(GroovySystem.getMetaClassRegistry().getMetaClass(supportedClass), supportedClass);
		}		
	}

    @SuppressWarnings("unchecked")
	protected void setMetaClass(final MetaClass metaClass, Class targetClass) {
        final MetaClass newMetaClass = new DelegatingMetaClass(metaClass) {
            @Override
            public Object getProperty(Object object, String property) {
                if (object instanceof Json) {
                    Json json = (Json) object;
                    return json.get(property);
                }else if (object instanceof GetAndPut) {
                    GetAndPut getAndPut = (GetAndPut) object;
                    return getAndPut.get(property);
                }
                return super.getProperty(object, property);
            }
        };
        GroovySystem.getMetaClassRegistry().setMetaClass(targetClass, newMetaClass);
        if(logger.isDebugEnabled())
        	logger.debug("groovy property access enabled for []",targetClass.getSimpleName());
    }
}
