package per.lian.deploy.client;

import java.io.File;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

import per.lian.deploy.pojo.SocketData;
import per.lian.utils.FileUtil;
import per.lian.utils.IOUtil;

public class SocketClient {

	private Socket socket;
	private InetSocketAddress destnation;
	private HeartBeatThread heartBeatThread;
	private CommandThread commandThread;
	private ClientReadThread socketReadThread;

	public static String ClientName;
	public static String ClientType;
	public static String WorkDir;
	
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
		commandThread = CommandThread.getInstance();
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

		String env = System.getenv("LDeploy");
		if (StringUtils.isEmpty(env)) {
			System.err.println("环境变量LDeploy未设置");
			return ;
		}
		File confFile = ResourceUtils.getFile("classpath:client/" + env +"/client.properties");
		String conf = FileUtil.readFileContent(confFile, "utf-8");
		JSONObject confJson = JSONObject.parseObject(conf);

		SocketClient.getInstance().init(confJson.getString("server_id"), confJson.getIntValue("server_port"));;
		
		ClientName = confJson.getString("client_name");
		ClientType = confJson.getString("client_type");
		WorkDir = confJson.getString("work_dir");
	}
}