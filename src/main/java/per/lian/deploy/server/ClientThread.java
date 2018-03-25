package per.lian.deploy.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import per.lian.deploy.pojo.SocketData;
import per.lian.deploy.pojo.SocketDataType;
import per.lian.utils.DateUtil;
import per.lian.utils.IOUtil;

/**
 * 客户端线程, 每个客户端对应一个线程
 * @author goalsword
 *
 */
public class ClientThread extends Thread implements SocketDataType {
	
	private final static int MSG_LIMIT = 3000;
	
	private Socket socket;
	private SocketServer socketServer;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private Queue<String> msgQueue = new LinkedList<String>();
	
	private String ip;
	private String lastHeartBeatTime = null;
	
	private String clientName;
	private ClientInfo clientInfo;


	public ClientThread(SocketServer socketServer, Socket socket) throws Exception {
		
		this.socketServer = socketServer;
		this.socket = socket;
		this.ip = socket.getInetAddress().getHostAddress();
		this.out = new ObjectOutputStream(socket.getOutputStream());
		this.in = new ObjectInputStream(socket.getInputStream());
		
		System.out.printf("ip:%s 已连接\r\n", ip);
	}

	public void execute(String cmd) throws Exception {
		
		this.out.writeObject(SocketData.SERVER_CMD(cmd));
	}
	
	public void sendFile(File file, String path) throws Exception {
		
		this.out.writeObject(SocketData.SERVER_FILE(file, path));
	}
	

	public void sendShutdown() throws Exception {
		
		this.out.writeObject(SocketData.SERVER_SHUTDOWN());
	}
	
	/**
	 * 一键部署
	 * @param type 
	 * @throws Exception
	 */
	public void oneKeyDeploy(String version) throws Exception{
		
		ServerFileManager.generaterMd5File(clientInfo.getType(), version);
		this.out.writeObject(new SocketData(SERVER_ONEKEY_DEPLOY));
	}
	
	@Override
	public void destroy() {
		
		IOUtil.close(out);
		IOUtil.close(in);
		IOUtil.close(socket);
	}
	
	@Override
	public void run() {
		
		while(true && !this.isInterrupted()) {
			try {
				SocketData socketObj = (SocketData) in.readObject();
				
				_handle(socketObj);
			} catch (Exception e) {
				
				this.interrupt();
			}
		}
	}

	private void _handle(SocketData socketObj) throws Exception {
		
		switch(socketObj.getType()){
		case CLIENT_INFO:
			System.out.println("client info:" + socketObj.getStringData());
			socketServer.add(this, socketObj.getStringData());
			break;
		case CLIENT_CMD_MSG:
			System.out.println("client cmd msg:" + socketObj.getStringData());
			msgQueue.offer(socketObj.getStringData());
			if(msgQueue.size() > MSG_LIMIT){
				msgQueue.poll();
			}
			break;
		case CLIENT_CMD_HEART:
			//收到心跳
			System.out.println("client heart beat:" + DateUtil.getDateYMDHMS());
			this.lastHeartBeatTime = DateUtil.getDateYMDHMS();
			break;
		}
	}

	public String getIp() {
		return ip;
	}

	public String getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
}