package org.duguo.xdir.rest.jersey;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: gdu
 * Date: 03/03/2012
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class Tester {
    public static void main(String[] args) throws Exception{


        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new File("/Users/gdu/projects/github/mrduguo/xdir/rest/bin/src/main/assembly/data/jcr/init/import/apps/ifdev_8080/pages/wadl/jcr.properties_xsl")));
        transformer.transform(new StreamSource(new File("/Users/gdu/Downloads/application.wadl")), new StreamResult(new File("/Users/gdu/Downloads/application.html")));
    }
}
