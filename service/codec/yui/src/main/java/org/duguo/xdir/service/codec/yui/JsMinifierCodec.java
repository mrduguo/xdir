package org.duguo.xdir.service.codec.yui;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;

public class JsMinifierCodec implements StringCodec,DynamicService {
    private static final Logger logger = LoggerFactory.getLogger(JsMinifierCodec.class);

    @Override
    public String apply(String input) throws Exception {
        if (logger.isTraceEnabled()) logger.trace("> apply {}", input);
        StringReader sr = new StringReader(input);
        com.yahoo.platform.yui.compressor.JavaScriptCompressor compressor = new com.yahoo.platform.yui.compressor.JavaScriptCompressor(sr, new ErrorReporter() {
            public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    logger.warn(message);
                } else {
                    logger.warn(line + ':' + lineOffset + ':' + message);
                }
            }

            public void error(String message, String sourceName,
                              int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    logger.error(message);
                } else {
                    logger.error(line + ':' + lineOffset + ':' + message);
                }
            }

            public EvaluatorException runtimeError(String message, String sourceName,
                                                   int line, String lineSource, int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });
        StringWriter result = new StringWriter();
        compressor.compress(result, getLineBreakPos(), isMunge(), isVerbose(), isPreserveSemiColons(), isDisableOptimizations());
        if (logger.isTraceEnabled()) logger.trace("< apply {}", result);
        return result.toString();
    }

    @Override
    public String apply(String input, Object model) throws Exception {
        return apply(input);
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.codec.js.service.name", "jsCodec");
    }

    //Minify only. Do not obfuscate local symbols.
    public static boolean isMunge() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.js.nomunge", "true"));
    }

    //Display informational messages and warnings
    public static boolean isVerbose() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.js.verbose", "false"));
    }

    //Preserve unnecessary semicolons (such as right before a '}')
    public static boolean isPreserveSemiColons() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.js.preserve.semicolons", "false"));
    }

    //Disable all the built-in micro optimizations.
    public static boolean isDisableOptimizations() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.js.disable.optimizations", "false"));
    }


    // Insert a line break after the specified column number
    public static int getLineBreakPos() {
        return Integer.parseInt(System.getProperty("xdir.service.codec.js.line.break.pos", "10"));
    }
}
