package org.duguo.xdir.core.internal.cache;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CacheOutputStream extends ServletOutputStream {
	private OutputStream content;
	
	public CacheOutputStream(OutputStream content) {
		this.content = content;
	}

	public void write(int i) throws IOException {
		content.write(i);
	}


	public void close() throws IOException {
		content.close();
	}

	public void flush() throws IOException {
		content.flush();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		content.write(b, off, len);
	}

	public void write(byte[] b) throws IOException {
		content.write(b);
	}
	
	// Unimplemented methods
	
	public void print(boolean b) throws IOException {
		throw new RuntimeException("Unimplemented method print(boolean b)");
	}

	
	public void print(char c) throws IOException {
		throw new RuntimeException("Unimplemented method print(char c)");
	}

	
	public void print(double d) throws IOException {
		throw new RuntimeException("Unimplemented method print(double d)");
	}

	
	public void print(float f) throws IOException {
		throw new RuntimeException("Unimplemented method print(float f)");
	}

	
	public void print(int i) throws IOException {
		throw new RuntimeException("Unimplemented method print(int i)");
	}

	
	public void print(long l) throws IOException {
		throw new RuntimeException("Unimplemented method print(long l)");
	}

	
	public void print(String s) throws IOException {
		throw new RuntimeException("Unimplemented method print(String s)");
	}

	
	public void println() throws IOException {
		throw new RuntimeException("Unimplemented method println()");
	}

	
	public void println(boolean b) throws IOException {
		throw new RuntimeException("Unimplemented method println(boolean b)");
	}

	
	public void println(char c) throws IOException {
		throw new RuntimeException("Unimplemented method println(char c)");
	}

	public void println(double d) throws IOException {
		throw new RuntimeException("Unimplemented method println(double d)");
	}

	
	public void println(float f) throws IOException {
		throw new RuntimeException("Unimplemented method println(float f)");
	}

	
	public void println(int i) throws IOException {
		throw new RuntimeException("Unimplemented method println(int i)");
	}

	public void println(long l) throws IOException {
		throw new RuntimeException("Unimplemented method println(long l)");
	}

	public void println(String s) throws IOException {
		throw new RuntimeException("Unimplemented method println(String s)");
	}


}
