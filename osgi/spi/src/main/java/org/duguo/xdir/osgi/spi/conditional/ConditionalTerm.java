package org.duguo.xdir.osgi.spi.conditional;

public interface ConditionalTerm {
	public boolean eval(String... params);
	public int numberOfParams();
}
