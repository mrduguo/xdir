package org.duguo.xdir.osgi.bootstrap.conditional.term;

import java.io.File;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;


/**
 * pass if the expected file/folder exist
 * 
 * Samples:
 * if__exists__c-u003A--u005C-Windows		// c:\Windows
 * if__exists__-u002F-tmp		// /tmp
 * 
 * @author mrduguo
 *
 */
public class ExistsTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		File targetFile=new File(params[0]);
		return targetFile.exists();
	}

	public int numberOfParams() {
		return 1;
	}

}
