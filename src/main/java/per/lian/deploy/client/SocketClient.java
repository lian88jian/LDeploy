package per.lian.deploy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import per.lian.deploy.pojo.SocketData;

public class SocketClient {

	private Socket socket;
	private InetSocketAddress destnation;
	private HeartBeatThread heartBeatThread;
	private CommandThread commandThread;
	private ClientReadThread socketReadThread;
	
	/**
	 * 
	 * @param ip 服务器ip
	 * @param port 服务器端口
	 * @throws Exception
	 */
	public SocketClient(String ip, int port) {
		
		this.destnation = new InetSocketAddress(ip, port);
		
		//执行命令的线程
		commandThread = CommandThread.getInstance();
		commandThread.start();
		
		//读取数据的线程
		socketReadThread = ClientReadThread.getInstance();
		socketReadThread.start();
		
		//心跳线程
		heartBeatThread = new HeartBeatThread(this, 5000);
		heartBeatThread.start();
	}
	
	public void connect(){
		
		System.out.println("server connecting...");
		try {
			socket = new Socket();
			socket.connect(destnation, 5000);
		} catch (Exception e) {
			System.out.printf("connect %s:%d failed...\n", destnation.getHostName(), destnation.getPort());
			return ;
		}
			
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			heartBeatThread.init(out);
			commandThread.init(out);
			socketReadThread.init(socket.getInputStream());
			
			out.writeObject(SocketData.CLIENT_INFO());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init io stream fail..." + e.getMessage());
			try {
				socket.close();
			} catch (Exception e1) {
			}
			return ;
		}
		System.out.println("connect server success");
	}

	public static void main(String[] args) throws Exception {
		
		new SocketClient("127.0.0.1", 8888);
	}
}