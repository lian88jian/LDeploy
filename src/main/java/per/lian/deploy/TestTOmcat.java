package per.lian.deploy;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import per.lian.deploy.client.SocketClient;
import per.lian.utils.ProcessUtil;

public class TestTOmcat {
	
	//https://blog.csdn.net/tiantang_1986/article/details/51306536
	public static void main(String[] args) throws Exception {
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.inheritIO().environment().put("JAVA_OPTS", "-Dfile.encoding=UTF8 -Dsun.jnu.encoding=UTF8");
		pb.environment().put("CATALINA_HOME", "E:/software/servers/apache-tomcat-7.0.69");
		
		pb.command("E:\\software\\servers\\apache-tomcat-7.0.69\\bin\\catalina.bat", "run");
		Process process = pb.start();
//		
		Thread.sleep(550000);
		
//		long pid = ProcessUtil.getPid(process);
//		System.out.println(pid);
//		System.out.println(process.isAlive());
//		System.out.println(process.exitValue());
//		process.destroy();
		
//		ProcessBuilder pb = new ProcessBuilder();
//		Map<String, String> env = pb.environment();
//		pb.inheritIO();
//		pb.command("java", "-version");
//		Process process = pb.start();
	}
}
