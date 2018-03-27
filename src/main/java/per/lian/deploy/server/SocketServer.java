package per.lian.deploy.server;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import per.lian.utils.FileUtil;

public class SocketServer {
	
	/**
	 * 客户端map
	 */
	private Map<String, ClientInfo> clientMap = new ConcurrentHashMap<String, ClientInfo>();
	private final int port;
	
	public static String WorkDir;
	
	public SocketServer(int port, JSONArray jsonArray) {
		this.port = port;
		for(int i = 0; i < jsonArray.size(); i ++) {
			JSONObject clientJson = jsonArray.getJSONObject(i);
			clientMap.put(clientJson.getString("name"), new ClientInfo(clientJson));
		}
	}

	/**
	 * 给客户端线程调用的
	 * @param clientThread
	 * @param clientJsonInfo 
	 */
	public void add(ClientThread clientThread, String clientJsonInfo) throws Exception{
		
		JSONObject json = JSONObject.parseObject(clientJsonInfo);
		String remoteName = json.getString("clientName");
		String remoteOs = json.getString("osName");
		
		if(!clientMap.containsKey(remoteName)) {
			//不包含, 直接关闭
			clientThread.sendShutdown();
			return ;
		}
		//可能是断线重连, 废弃以前的
		ClientInfo clientInfo = clientMap.get(remoteName);
		ClientThread abondonClient = clientInfo.getClientThread();
		if(abondonClient != null){
			abondonClient.destroy();
		}
		
		clientInfo.setClientThread(clientThread);
		
		
		clientInfo.setOs(remoteOs);
		clientThread.setClientInfo(clientInfo);
		
		System.out.printf("client [%s] connected, os:%s, ip:%s", remoteName, clientInfo.getOs(), clientThread.getIp());
	}
	
	@SuppressWarnings("resource")
	public void start() throws Exception {
		ServerSocket server = new ServerSocket(port);
		
		System.out.println("start listen on port:" + port);
		while(true) {
			
			Socket socket = server.accept();
			
			System.out.println("rec connect from " + socket.getInetAddress().getHostName());
			ClientThread clientThread = new ClientThread(this, socket);
			clientThread.start();
		}
	}

	public static void main(String[] args) throws Exception {
		
		File confFile = ResourceUtils.getFile("classpath:server/server.json");
		String conf = FileUtil.readFileContent(confFile, "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);
		
		WorkDir = confJson.getString("work_dir");
		
		new SocketServer(confJson.getIntValue("port"), confJson.getJSONArray("clients")).start(); // 启动
	}

}