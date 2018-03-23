package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketReadThread extends Thread{
	
	private static SocketReadThread instance;
	private BufferedReader socketIn;
	private CommandThread commandThread;
	
	public static SocketReadThread getInstance() throws Exception {
		if(instance == null) {
			instance = new SocketReadThread();
		}
		return instance;
	}
	
	private SocketReadThread() throws Exception {
		
		commandThread = CommandThread.getInstance();
	}
	
	public void init(InputStream in) {
		this.socketIn = new BufferedReader(new InputStreamReader(in));
	}

	@Override
	public void run() {
		
		try {
			while(true){
				String cmd = socketIn.readLine();
				System.out.println("execute command:" + cmd);
				
				commandThread.execute(cmd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
