package org.duguo.xdir.core.internal.model;

import java.io.IOException;

public interface FormatService
{
    public static String FORMAT_FOLDER=".folder";
    
    public void resolveFormat(ModelImpl model) throws IOException;
    
    public String autoDetectFormat( ModelImpl model);

    public String resolveContentType( String fileName );

    public boolean isText( String fileName );

    public boolean isBinary( String fileName );
}
