package per.lian.deploy.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import per.lian.deploy.pojo.SocketConstants;
import per.lian.deploy.pojo.SocketData;
import per.lian.utils.DateUtil;
import per.lian.utils.IOUtil;

/**
 * 客户端线程, 每个客户端对应一个线程
 * @author goalsword
 *
 */
public class ClientThread extends Thread implements SocketConstants {
	
	private final static int MSG_LIMIT = 3000;
	private static Logger logger = Logger.getLogger(ClientThread.class);
	
	private Socket socket;
	private SocketServer socketServer;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private Queue<String> msgQueue = new LinkedList<String>();
	
	private String ip;
	private String lastHeartBeatTime = null;
	
	private ClientInfo clientInfo;


	public ClientThread(SocketServer socketServer, Socket socket) {
		
		try {
			
			this.socketServer = socketServer;
			this.socket = socket;
			this.ip = socket.getInetAddress().getHostAddress();
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			
			logger.info("ip:" + ip + " already connected");
		} catch(Exception e) {
			
			logger.error("ip:" + ip + " client connect error", e);
		}
	}

	public void execute(String cmd) throws Exception {
		
		this.out.writeObject(SocketData.SERVER_CMD(cmd));
	}
	
	/**
	 * 发送文件
	 * @param file 文件
	 * @param flowName 流程名称, 由客户端传来
	 * @param fileName 
	 * @param path 相对于版本目录的路径
	 * @throws Exception
	 */
	public void sendFile(File file, String flowName, String version, String fileName) throws Exception {
		
		this.out.writeObject(SocketData.SERVER_FILE(file, flowName, version, fileName));
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
		
		this.out.writeObject(new SocketData(SERVER_ONEKEY_DEPLOY, clientInfo.getType(), version));
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
				
				e.printStackTrace();
				this.interrupt();
			}
		}
	}

	private void _handle(SocketData socketObj) throws Exception {
		
		switch(socketObj.getType()){
		case CLIENT_INFO:
			//客户端身份信息
			socketServer.add(this, socketObj.getStringData());
			System.out.printf("client[%s] info:%s\r\n", clientInfo.getClientName(), socketObj.getStringData());
			break;
		case CLIENT_ERROR:
			System.out.printf("client[%s] error msg:%s\r\n", clientInfo.getClientName(), socketObj.getMsg_1());
			break;
		case CLIENT_CMD_MSG:
			//客户端控制台信息
			System.out.printf("client[%s] console:%s\r\n", clientInfo.getClientName(), socketObj.getStringData());
			msgQueue.offer(socketObj.getStringData());
			if(msgQueue.size() > MSG_LIMIT){
				msgQueue.poll();
			}
			break;
		case CLIENT_CMD_HEART:
			//收到心跳
//			System.out.printf("client[%s] heart beat:%s\r\n", clientInfo.getClientName(), DateUtil.getDateYMDHMS());
			this.lastHeartBeatTime = DateUtil.getDateYMDHMS();
			break;
		case CLIENT_REQUIRE_FILE:
			//客户端请求md5文件
			//msg1是流程,msg2是版本,msg3是文件名
			String flow = socketObj.getMsg_1();
			String version = socketObj.getMsg_2();
			String fileName = socketObj.getMsg_3();
			System.out.printf("client[%s] require version[%s] file[%s]\r\n", clientInfo.getClientName(), socketObj.getMsg_2(), socketObj.getMsg_3());
			//服务端目录 + 客户端类型(was-dubbo/was-web/egov-dubbo) + 客户端版本
			if("md5.txt".equals(fileName)) {
				ServerFileManager.generaterMd5File(clientInfo.getType(), version);
			}
			String filePath = _getVersionPath(version, fileName);
			this.sendFile(new File(filePath), flow, version, fileName);
			break;
		case CLIENT_NEXT_STEP:
			//客户端请求下一步动作
			System.out.printf("client[%s] next step:%s\r\n", clientInfo.getClientName(), socketObj.getMsg_3());
			socketObj.setType(SERVER_NEXT_STEP);
			this.out.writeObject(socketObj);
			break;
		}
	}
	
	private String _getVersionPath(String version, String filePath) {
		return SocketServer.WorkDir + SP + clientInfo.getType() + SP + version + SP + filePath;
	}

	public String getIp() {
		return ip;
	}

	public String getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}
	
}