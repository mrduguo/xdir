package org.duguo.xdir.core.internal.jcr.importor;

import javax.jcr.Node;
import java.io.InputStream;

public interface MmImportor {

	void importMm(Node node,InputStream inputStream) throws Exception;
}
