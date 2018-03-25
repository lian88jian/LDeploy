package per.lian.deploy.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer {

	private Map<String, ClientThread> clientMap = new ConcurrentHashMap<String, ClientThread>();
	private final int port;
	
	public SocketServer(int port) {
		this.port = port;
	}

	@SuppressWarnings("resource")
	public void start() throws Exception {
		ServerSocket server = new ServerSocket(port);
		
		System.out.println("start listen on port:" + port);
		while(true) {
			
			Socket socket = server.accept();
			
			System.out.println("somebody connected");
			ClientThread clientThread = new ClientThread(this, socket);
			clientThread.start();
		}
	}
	
	public void add(ClientThread clientThread){
		
		String clientName = clientThread.getClientName();
		if(clientMap.containsKey(clientName)) {
			//可能是断线重连, 废弃以前的
			ClientThread abondonClient = clientMap.get(clientName);
			abondonClient.destroy();
		}
		
		clientMap.put(clientName, clientThread);
	}

	public static void main(String[] args) throws Exception {

		new SocketServer(8888).start(); // 启动
	}

}