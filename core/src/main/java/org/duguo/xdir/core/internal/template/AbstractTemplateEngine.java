package org.duguo.xdir.core.internal.template;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.ResourceLoader;


public abstract class AbstractTemplateEngine implements TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger( AbstractTemplateEngine.class );
    
    private ResourceLoader templateStringLoader;
    private Map<String, Compressor> compressors;
    
    
    public int process( ModelImpl model,String templateName) throws Exception
    {
        return process( model, templateName,Model.NULL_STRING);
    }

    public String compress( String format, String rawString ) throws Exception
    {
        Compressor compressor=compressors.get( format );
        if(compressor!=null){
            long startTimestamp=System.currentTimeMillis();
            String result=compressor.compress( rawString );
            if(logger.isDebugEnabled()){
                int compressRate=result.length()*100/rawString.length();
                logger.debug("compressed raw "+format+" string from [{}] to [{}] in "+(System.currentTimeMillis()-startTimestamp)+" milliseconds, compress rate "+compressRate+"%",
                    String.valueOf( rawString.length() ),String.valueOf( result.length() ));
            }
            return result;
        }else{
            throw new RuntimeException("compress format ["+format+"] not supported");
        }
    }

    public Map<String, Compressor> getCompressors()
    {
        return compressors;
    }

    public void setCompressors( Map<String, Compressor> compressors )
    {
        this.compressors = compressors;
    }

    public void setTemplateStringLoader( ResourceLoader templateStringLoader )
    {
        this.templateStringLoader = templateStringLoader;
    }

    public ResourceLoader getTemplateStringLoader()
    {
        return templateStringLoader;
    }


}
