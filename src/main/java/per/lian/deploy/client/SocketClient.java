package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {

	private Socket socket;

	public SocketClient() throws Exception {

		this.socket = new Socket("127.0.0.1", 8888);
		
		CommandThread commandThread = CommandThread.getInstance();
		commandThread.init(socket.getOutputStream());
		commandThread.start();
		
		//读取的线程
		SocketReadThread socketReadThread = SocketReadThread.getInstance();
		socketReadThread.init(socket.getInputStream());
		socketReadThread.start();
	}

	public static void main(String[] args) throws Exception {
		
		new SocketClient();
	}
}