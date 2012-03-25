package org.duguo.xdir.core.internal.template.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/*
 * 
 * 
<#assign groovy = "org.duguo.xdir.core.internal.template.freemarker.GroovyDirective"?new()>
<@groovy myname="Guo">
    def model=freemarker.template.utility.DeepUnwrap.unwrap(env.dataModel.model)
    
	// run console
    groovy.ui.Console console = new groovy.ui.Console()
    console.setVariable("env", env)
    console.setVariable("params", params)
    console.run()

	// return cache version
	def version="cache version:"+env.dataModel.service.cache.version
	println version
	return version
</@groovy>
 */

public class GroovyDirective  implements TemplateDirectiveModel{
	
	private static final Logger logger = LoggerFactory.getLogger( GroovyDirective.class );

	@SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		if (body != null) {
			StringWriter sw=new StringWriter();
			sw.append( "model=freemarker.template.utility.DeepUnwrap.unwrap(env.dataModel.model)\n" );
            body.render(sw);
            if(logger.isDebugEnabled())
                logger.debug("grovvy script body:\n{}",sw.toString());

    		Binding binding = new Binding();
    		binding.setVariable("env", env);
    		binding.setVariable("params", params);
    		binding.setVariable("loopVars", loopVars);
    		GroovyShell shell = new GroovyShell(binding);
    		Object result = null;
    		try{
        		result = shell.evaluate(sw.toString());
    		}catch(Exception ex){
    		    throw new TemplateException("failed to evaluate script:\n"+sw.toString(),ex,env);
    		}
            if(result!=null){
                env.getOut().write(result.toString());
            }
        } else {    
            throw new TemplateException("missing grovvy script body",env);
        }

	}
}
