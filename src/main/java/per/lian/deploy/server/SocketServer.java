package per.lian.deploy.server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

import per.lian.utils.FileUtil;

public class SocketServer {

	private Map<String, ClientThread> clientMap = new ConcurrentHashMap<String, ClientThread>();
	private final int port;
	
	public SocketServer(int port, Map<String, ?> clientMap) {
		this.port = port;
	}

	/**
	 * 给客户端线程调用的
	 * @param clientThread
	 */
	public void add(ClientThread clientThread){
		
		String clientName = clientThread.getClientName();
		if(clientMap.containsKey(clientName)) {
			//可能是断线重连, 废弃以前的
			ClientThread abondonClient = clientMap.get(clientName);
			abondonClient.destroy();
		}
		
		clientMap.put(clientName, clientThread);
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
			
			clientThread.sendFile(new File("C:\\Users\\goalsword\\Desktop\\infoLog.log"), "20180325");
		}
	}

	public static void main(String[] args) throws Exception {
		
		File confFile = ResourceUtils.getFile("classpath:config/test.properties");
		String conf = FileUtil.readFileContent(confFile, "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);
		
		new SocketServer(confJson.getIntValue("port"), confJson.getJSONObject("clients")).start(); // 启动
	}

}