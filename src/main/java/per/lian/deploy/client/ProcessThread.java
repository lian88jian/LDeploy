package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Platform;

import per.lian.deploy.pojo.SocketData;

public class ProcessThread extends Thread {

	private static ProcessThread instance;
	private BufferedReader processIn;
	private PrintWriter processOut;
	private ObjectOutputStream socketOut;

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
	
	public void startProcess() throws Exception {
		ProcessBuilder pb = new ProcessBuilder();
		Map<String, String> env = pb.environment();
		env.putAll(SocketClient.envMap);
		if(Platform.isWindows()) {
			pb.command(SocketClient.startWindows);
		} else {
			pb.command(SocketClient.startLinux);
		}
		Process process = pb.start();
		
		processIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
		processOut = new PrintWriter(process.getOutputStream());
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				
				String cmdMsg = processIn.readLine();
				if(StringUtils.isNotEmpty(cmdMsg)){
					
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
