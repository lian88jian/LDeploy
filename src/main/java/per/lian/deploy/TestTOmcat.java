package per.lian.deploy;

import java.io.File;
import java.io.IOException;

import per.lian.utils.ProcessUtil;

public class TestTOmcat {
	
	//https://blog.csdn.net/tiantang_1986/article/details/51306536
	public static void main(String[] args) throws Exception {
		
		File file = new File("F:\\program\\java\\j2ee\\tomcat\\apache-tomcat-7.0.70\\bin");
		System.out.println(file.exists());
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(file).inheritIO().environment().put("JAVA_OPTS", "-Dfile.encoding=UTF8 -Dsun.jnu.encoding=UTF8");
		
		pb.command("F:\\program\\java\\j2ee\\tomcat\\apache-tomcat-7.0.70\\bin\\catalina.bat", "run");
		Process process = pb.start();
		
		Thread.sleep(15000);
		
//		long pid = ProcessUtil.getPid(process);
//		System.out.println(pid);
//		System.out.println(process.isAlive());
//		System.out.println(process.exitValue());
//		process.destroy();
	}
}
