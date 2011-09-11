package org.duguo.xdir.osgi.bootstrap;


public class Tester {

	public  static void main(String...strings ) throws Exception{
		//Process process = Runtime.getRuntime().exec("ping localhost");
		ProcessBuilder pb=new ProcessBuilder("ping","localhost");
		pb.redirectErrorStream(true);
		pb.start().destroy();
		System.out.println(pb);
	}
}
