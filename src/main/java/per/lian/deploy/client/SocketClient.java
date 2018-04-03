package per.lian.deploy.client;

import java.io.File;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

import per.lian.deploy.pojo.SocketData;
import per.lian.utils.FileUtil;
import per.lian.utils.IOUtil;

public class SocketClient {

	private Socket socket;
	private InetSocketAddress destnation;
	private HeartBeatThread heartBeatThread;
	private ProcessThread commandThread;
	private ClientReadThread socketReadThread;

	public static String ClientName;
	public static String ClientType;
	public static String WorkDir;
	public static int pidPort;
	public static String startType;
	public static String startCmd;
	public static String tomcatDir;
	public static Map<String, String> envMap = new HashMap<>();
	
	public static String projectVersion;
	
	private static SocketClient instance = new SocketClient();
	
	public static SocketClient getInstance() {
		return instance;
	}
	private SocketClient(){
		
	}
	/**
	 * 
	 * @param ip
	 *            服务器ip
	 * @param port
	 *            服务器端口
	 * @return 
	 * @throws Exception
	 */
	public void init(String ip, int port) {

		this.destnation = new InetSocketAddress(ip, port);

		// 执行命令的线程
		commandThread = ProcessThread.getInstance();
		commandThread.start();

		// 读取数据的线程
		socketReadThread = ClientReadThread.getInstance();
		socketReadThread.start();

		// 心跳线程
		heartBeatThread = new HeartBeatThread(this, 5000);
		heartBeatThread.start();
	}

	public void connect() {

		System.out.println("server connecting...");
		try {
			socket = new Socket();
			socket.connect(destnation, 5000);
		} catch (Exception e) {
			System.out.printf("connect %s:%d failed...\n", destnation.getHostName(), destnation.getPort());
			return;
		}

		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			heartBeatThread.init(out);
			commandThread.init(out);
			socketReadThread.init(out, socket.getInputStream());

			out.writeObject(SocketData.CLIENT_INFO());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init io stream fail..." + e.getMessage());
			try {
				socket.close();
			} catch (Exception e1) {
			}
			return;
		}
		System.out.println("connect server success");
	}
	
	public void shutdown() {
		
		IOUtil.close(socket);
	}
	/**
	 * 127.0.0.1 8888 WasDubbo_131
	 * C:/Users/goalsword/git/LDeploy/target/classes/
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		ResourcePatternResolver log4jresolver = new PathMatchingResourcePatternResolver();
		Resource log4jresource = log4jresolver.getResource("client/log4j.properties");
		PropertyConfigurator.configure(log4jresource.getInputStream());
		
		String env = System.getenv("LDeploy");
		if (StringUtils.isEmpty(env)) {
			System.err.println("环境变量LDeploy未设置");
			return ;
		}
		env = env.trim();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource("classpath:client/" + env+ ".json");
		System.out.println("LDeploy:" + env);
		if(!resource.exists()) {
			System.err.println("配置文件不存在:" + resource.getDescription());
			return ;
		}
		String conf = FileUtil.readFileContent(resource.getInputStream(), "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);

		SocketClient.getInstance().init(confJson.getString("server_ip"), confJson.getIntValue("server_port"));;
		
		ClientName = confJson.getString("client_name");
		ClientType = confJson.getString("client_type");
		WorkDir = confJson.getString("work_dir");
		pidPort = confJson.getIntValue("pid_port");	//服务占用的端口,用于检查是否在运行
		startCmd = confJson.getString("start_cmd");	//启动命令
		startType = confJson.getString("start_type");
		tomcatDir = confJson.getString("tomcat_dir");
		JSONObject envJson = confJson.getJSONObject("env");	//初始化启动环境变量配置
		for(String key : envJson.keySet()) {
			envMap.put(key, envJson.getString(key));
		}
	}
}