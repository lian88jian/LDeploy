package per.lian.deploy.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketCommand extends Thread {
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	

	public SocketCommand(Socket socket) throws Exception {
		
		this.socket = socket;
		this.out = new PrintWriter(socket.getOutputStream());
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void execute() throws Exception {
		
		out.println("cmd");
		out.flush();
	}
	
	@Override
	public void run() {
		
		while(true) {
			try {
				String str = in.readLine();
				System.out.println(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}