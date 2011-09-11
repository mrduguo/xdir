package org.duguo.xdir.osgi.spi.conditional;

public interface ConditionalService {
	public boolean isConditionalString(String source);
	public boolean eval(String source);
	public void registTerm(String key,ConditionalTerm term);
}
