package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

public class CommandThread extends Thread {

	private static CommandThread instance;
	private BufferedReader processIn;
	private PrintWriter processOut;
	private PrintWriter socketOut;

	public static CommandThread getInstance() throws Exception {

		if (instance == null) {
			instance = new CommandThread();
		}
		return instance;
	}

	private CommandThread() throws Exception {
		
	}
	
	public void init(OutputStream out) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("cmd");
		
		processIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
		processOut = new PrintWriter(process.getOutputStream());
		
		socketOut = new PrintWriter(out);
	}
	
	public void execute(String cmd) {
		
		processOut.println(cmd);
		processOut.flush();
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				String str = processIn.readLine();
				socketOut.println(str);
				socketOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
