package org.duguo.xdir.osgi.bootstrap.i18n;

import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class MessagesLoaderTest {

    @Test
    public void shouldPrintChineseChars() throws Exception{
        Map<String,String> props = MessagesLoader.load(getClass().getResource("/org/duguo/xdir/osgi/bootstrap/i18n/Messages_zh_CN.txt").getFile());
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(System.out, "UTF-8");
        System.out.println("sysout (java):调试");
        outputStreamWriter.write("outputStreamWriter (java):调试\n");
        outputStreamWriter.write("outputStreamWriter (encoding):\u8c03\u8bd5\n");
        outputStreamWriter.flush();
        System.out.println("sysout (property):"+props.get("string.logger.level.debug"));
        assertEquals("\u8c03\u8bd5",props.get("string.logger.level.debug"));
    }
}
