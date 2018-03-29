package per.lian.deploy.server;

import java.io.File;
import java.io.FileFilter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import per.lian.deploy.pojo.SocketConstants;
import per.lian.utils.FileUtil;
import per.lian.utils.ResourceUtil;

public class SocketServer extends Thread {

	private static Logger logger = Logger.getLogger(SocketServer.class);
	/**
	 * 客户端map
	 */
	private Map<String, ClientInfo> clientMap = new ConcurrentHashMap<String, ClientInfo>();
	private final int port;

	public static String WorkDir;

	private static SocketServer instance;
	private static ServerSocket server;

	public static SocketServer getInstance() {
		if (instance == null) {
			try {
				instance = new SocketServer();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private SocketServer() throws Exception {

		String conf = FileUtil.readFileContent(ResourceUtil.getInputStream("server/conf.json"), "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);

		WorkDir = confJson.getString("work_dir");
		port = confJson.getIntValue("port");
		JSONArray jsonArray = confJson.getJSONArray("clients");

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject clientJson = jsonArray.getJSONObject(i);
			clientMap.put(clientJson.getString("name"), new ClientInfo(clientJson));
		}
	}

	/**
	 * 给客户端线程调用的
	 * 
	 * @param clientThread
	 * @param clientJsonInfo
	 */
	public void add(ClientThread clientThread, String clientJsonInfo) throws Exception {

		JSONObject json = JSONObject.parseObject(clientJsonInfo);
		String remoteName = json.getString("clientName");
		String remoteOs = json.getString("osName");

		if (!clientMap.containsKey(remoteName)) {
			// 不包含, 直接关闭
			clientThread.sendShutdown();
			return;
		}
		// 可能是断线重连, 废弃以前的
		ClientInfo clientInfo = clientMap.get(remoteName);
		ClientThread abondonClient = clientInfo.getClientThread();
		if (abondonClient != null) {
			abondonClient.destroy();
		}

		clientInfo.setClientThread(clientThread);

		clientInfo.setOs(remoteOs);
		clientThread.setClientInfo(clientInfo);

		System.out.printf("client [%s] connected, os:%s, ip:%s", remoteName, clientInfo.getOs(), clientThread.getIp());

		// add test command code here
//		clientThread.oneKeyDeploy("20180324");
	}
	
	public Collection<ClientInfo> getClientList() {
		return clientMap.values();
	}
	
	public ClientInfo getClientByName(String clientName) {
		return clientMap.get(clientName);
	}
	
	public static List<String> getVersionList(String projectType) {
		
		File projectDir = new File(WorkDir + SocketConstants.SP + projectType);
		List<String> vL = new ArrayList<>();
		List<File> fileList = FileUtil.getDirs(projectDir);
		for(File _f : fileList) {
			vL.add(_f.getName());
		}
		return vL;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			
			logger.info("deploy server start listen on port:" + port);
			while (true) {

				Socket socket = server.accept();

				System.out.println("rec connect from " + socket.getInetAddress().getHostName());
				ClientThread clientThread = new ClientThread(this, socket);
				clientThread.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource("server\\log4j.properties");
		PropertyConfigurator.configure(resource.getInputStream());

		File confFile = ResourceUtils.getFile("classpath:server/server.json");
		String conf = FileUtil.readFileContent(confFile, "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);

		WorkDir = confJson.getString("work_dir");

		new SocketServer().start(); // 启动

	}


}