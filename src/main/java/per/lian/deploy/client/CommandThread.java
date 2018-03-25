package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

import per.lian.deploy.pojo.SocketData;

public class CommandThread extends Thread {

	private static CommandThread instance;
	private BufferedReader processIn;
	private PrintWriter processOut;
	private ObjectOutputStream socketOut;

	public static CommandThread getInstance() {

		if (instance == null) {
			instance = new CommandThread();
		}
		return instance;
	}

	private CommandThread() {
		
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec("cmd");
			processIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
			processOut = new PrintWriter(process.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init(ObjectOutputStream out) throws Exception {
		
		socketOut = out;
	}
	
	public void execute(String cmd) {
		
		processOut.println(cmd);
		processOut.flush();
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
