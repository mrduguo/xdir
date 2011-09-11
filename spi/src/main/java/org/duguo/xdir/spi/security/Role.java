package org.duguo.xdir.spi.security;

public interface Role {
	public static final int ANONYMOUS	=0;
	public static final int USER		=10;
	public static final int READER		=100;
	public static final int WRITER		=1000;
	public static final int OWNER		=10000;
	public static final int ADMIN		=100000;
}
