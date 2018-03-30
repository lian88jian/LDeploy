package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Platform;

import per.lian.deploy.pojo.SocketData;
import per.lian.utils.FileUtil;

public class ProcessThread extends Thread {

	private static ProcessThread instance;
	private BufferedReader processIn;
//	private PrintWriter processOut;
	private ObjectOutputStream socketOut;
	private Process process;

	public static ProcessThread getInstance() {

		if (instance == null) {
			instance = new ProcessThread();
		}
		return instance;
	}

	private ProcessThread() {
		
	}
	
	public void init(ObjectOutputStream out) throws Exception {
		
		socketOut = out;
	}
	
	public void startProcess(String version) throws Exception {
		
		if("tomcat".equals(SocketClient.startType)) {
			//启动tomcat, 需要修改server.xml
			_modifyTomcatConf(version);
			SocketClient.envMap.put("CATALINA_HOME", "E:/software/servers/apache-tomcat-7.0.69");
		}
		
		if(process == null || !process.isAlive()) {
			
			ProcessBuilder pb = new ProcessBuilder();
			Map<String, String> env = pb.environment();
			env.putAll(SocketClient.envMap);
			pb.inheritIO().command(SocketClient.startCmd.replace("${version}", version).split(" "));
			pb.redirectErrorStream(true).redirectError(Redirect.PIPE).redirectInput(Redirect.PIPE).redirectOutput(Redirect.PIPE);
			process = pb.start();
			processIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			processOut = new PrintWriter(process.getOutputStream());
		}
		
//		ProcessBuilder pb = new ProcessBuilder();
//		pb.inheritIO().environment().put("JAVA_OPTS", "-Dfile.encoding=UTF8 -Dsun.jnu.encoding=UTF8");
//		pb.environment().put("CATALINA_HOME", "E:/software/servers/apache-tomcat-7.0.69");
//		
//		pb.command("E:\\software\\servers\\apache-tomcat-7.0.69\\bin\\catalina.bat", "run");
//		Process process = pb.start();
		
	}
	
	private void _modifyTomcatConf(String version) throws IOException {
		
		File confFile = new File(SocketClient.tomcatDir + "/conf/server.xml");
		String fileContent = FileUtil.readFileContent(confFile, "utf-8");
		//appBase=\"[\w\:\_\-\\]*\"
		fileContent = fileContent.replaceAll("appBase=\\\"[\\w\\:\\_\\-\\\\/]*\\\"", "appBase=\"" + SocketClient.WorkDir + "/" + version + "\"");
		FileUtil.createFileWithBytes(confFile.getAbsolutePath(), fileContent.getBytes("utf-8"));
	}
	
//	public static void main(String[] args) throws IOException {
//		
//		SocketClient.WorkDir = "E:/temp/temps/tomcat_client";
//		SocketClient.tomcatDir = "E:\\software\\servers\\apache-tomcat-7.0.69";
//		new ProcessThread()._modifyTomcatConf("22");
//	}

	@Override
	public void run() {
		
		while(true) {
			try {
				
				String cmdMsg = processIn.readLine();
				if(StringUtils.isNotEmpty(cmdMsg)){
					System.out.println(cmdMsg);
					socketOut.writeObject(SocketData.CLIENT_CMD_MSG(cmdMsg));
				}
			} catch (Exception e) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
