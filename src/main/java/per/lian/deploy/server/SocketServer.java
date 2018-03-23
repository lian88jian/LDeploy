package per.lian.deploy.server;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	private final int port;

	public SocketServer(int port) {
		this.port = port;
	}

	@SuppressWarnings("resource")
	public void start() throws Exception {
		ServerSocket server = new ServerSocket(port);
		
		while(true) {
			
			Socket socket = server.accept();
			
			System.out.println("somebody connected");
			SocketCommand socketCommand = new SocketCommand(socket);
			socketCommand.start();
			
			while(true) {
				socketCommand.execute();
				Thread.sleep(5000);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		new SocketServer(8888).start(); // 启动
	}
}