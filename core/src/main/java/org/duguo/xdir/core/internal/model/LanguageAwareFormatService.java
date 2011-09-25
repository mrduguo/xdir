package org.duguo.xdir.core.internal.model;


import java.util.Map;


public class LanguageAwareFormatService extends FormatServiceImpl
{
    private Map<String, String> supportedLanguages;

    public String autoDetectFormat( ModelImpl model )
    {
        String format = super.autoDetectFormat( model );
        String acceptLanguage = model.getRequest().getHeader( "Accept-Language" );
        if ( supportedLanguages != null )
        {
            for ( Map.Entry<String, String> entry : supportedLanguages.entrySet() )
            {
                if ( acceptLanguage.matches( entry.getValue() ) )
                {
                    return "." + entry.getKey() + "_" + format.substring( 1 );
                }
            }
        }
        return format;
    }


    public void setSupportedLanguages( Map<String, String> supportedLanguages )
    {
        this.supportedLanguages = supportedLanguages;
    }
}
