package org.duguo.xdir.core.internal.template.compressor;

import java.io.StringReader;
import java.io.StringWriter;

import org.duguo.xdir.core.internal.template.Compressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class JavaScriptCompressor implements Compressor
{

    private int linebreak=10;                     //Insert a line break after the specified column number
    private boolean munge=true;                   //Minify only. Do not obfuscate local symbols.
    private boolean verbose=false;                //Display informational messages and warnings
    private boolean preserveAllSemiColons=false;  //Preserve unnecessary semicolons (such as right before a '}')
    private boolean disableOptimizations=false;   //Disable all the built-in micro optimizations.
    
    public String compress( String rawString ) throws Exception
    {
        StringReader sr=new StringReader( rawString );
        com.yahoo.platform.yui.compressor.JavaScriptCompressor compressor= new com.yahoo.platform.yui.compressor.JavaScriptCompressor(sr, new ErrorReporter() {

                                public void warning(String message, String sourceName,
                                        int line, String lineSource, int lineOffset) {
                                    if (line < 0) {
                                        System.err.println("\n[WARNING] " + message);
                                    } else {
                                        System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
                                    }
                                }

                                public void error(String message, String sourceName,
                                        int line, String lineSource, int lineOffset) {
                                    if (line < 0) {
                                        System.err.println("\n[ERROR] " + message);
                                    } else {
                                        System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
                                    }
                                }

                                public EvaluatorException runtimeError(String message, String sourceName,
                                        int line, String lineSource, int lineOffset) {
                                    error(message, sourceName, line, lineSource, lineOffset);
                                    return new EvaluatorException(message);
                                }
                            });
        StringWriter sw=new StringWriter();
        compressor.compress( sw, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations );
        return sw.toString();
    }

    public int getLinebreak()
    {
        return linebreak;
    }

    public void setLinebreak( int linebreak )
    {
        this.linebreak = linebreak;
    }

    public boolean isMunge()
    {
        return munge;
    }

    public void setMunge( boolean munge )
    {
        this.munge = munge;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose( boolean verbose )
    {
        this.verbose = verbose;
    }

    public boolean isPreserveAllSemiColons()
    {
        return preserveAllSemiColons;
    }

    public void setPreserveAllSemiColons( boolean preserveAllSemiColons )
    {
        this.preserveAllSemiColons = preserveAllSemiColons;
    }

    public boolean isDisableOptimizations()
    {
        return disableOptimizations;
    }

    public void setDisableOptimizations( boolean disableOptimizations )
    {
        this.disableOptimizations = disableOptimizations;
    }

}
