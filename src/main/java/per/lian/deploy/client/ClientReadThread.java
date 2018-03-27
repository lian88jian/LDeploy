package per.lian.deploy.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import per.lian.deploy.pojo.SocketData;
import per.lian.deploy.pojo.SocketConstants;
import per.lian.utils.FileUtil;
import per.lian.utils.ProcessUtil;

public class ClientReadThread extends Thread implements SocketConstants {

	private static ClientReadThread instance;
	private ObjectInputStream socketIn;
	private ProcessThread commandThread;
	private ObjectOutputStream out;

	public static ClientReadThread getInstance() {
		if (instance == null) {
			instance = new ClientReadThread();
		}
		return instance;
	}

	private ClientReadThread() {

		commandThread = ProcessThread.getInstance();
	}

	public void init(ObjectOutputStream out, InputStream in) throws Exception {
		this.socketIn = new ObjectInputStream(in);
		this.out = out;
	}

	@Override
	public void run() {

		while (true) {
			try {

				SocketData socketData = (SocketData) socketIn.readObject();
				_handleServerData(socketData);
			} catch (Exception e) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void sendSocketData(SocketData socketData) throws Exception {
		out.writeObject(socketData);
	}

	private void _handleServerData(SocketData socketData) throws Exception {

		String _flowName;
		String _version;

		switch (socketData.getType()) {
		case SERVER_CMD:
			// 服务器要求执行cmd命令
//			commandThread.execute(socketData.getStringData());
			break;
		case SERVER_FILE:
			// 接收到服务器文件
			// msg1 是流程, msg2是版本, msg3是文件名
			_flowName = socketData.getMsg_1();
			_version = socketData.getMsg_2();
			String _fileName = socketData.getMsg_3();
			FileUtil.createFileWithBytes(SocketClient.WorkDir + _version, _fileName, socketData.getData());
			break;
		case SERVER_SHUTDOWN:
			System.err.println("服务端要求关闭程序");
			SocketClient.getInstance().shutdown();
			break;
		case SERVER_ONEKEY_DEPLOY:
			// 服务器发送一键部署指令
			String clientType = socketData.getMsg_1();
			// 比较服务器类型
			if (!SocketClient.ClientType.equals(clientType)) {
				out.writeObject(new SocketData(CLIENT_ERROR, "服务端与客户端类型不一致"));
			}
			_version = socketData.getMsg_2();
			// 校验客户端文件, 会自动下载文件
			ClientFileManager.checkProjectFiles(FLOW_ONE_KEY_DEPLOY, _version);
			
			// 下一步, 杀死进程
			socketData = new SocketData(CLIENT_NEXT_STEP, FLOW_ONE_KEY_DEPLOY, _version, STEP_KILL_PROCESS);
			ClientReadThread.getInstance().sendSocketData(socketData);
			// 下一步, 启动进程
			socketData = new SocketData(CLIENT_NEXT_STEP, FLOW_ONE_KEY_DEPLOY, _version, STEP_START_PROCESS);
			ClientReadThread.getInstance().sendSocketData(socketData);
			break;
		case SERVER_NEXT_STEP:
			// 服务器发送下一步指令
			_flowName = socketData.getMsg_1();
			_version = socketData.getMsg_2();
			_handleNextStep(_flowName, _version, STEP_VALID_WITH_MD5_FILE);
		}
	}

	private void _handleNextStep(String flowName, String version, String nextStep) throws Exception {

		switch (flowName) {
		case FLOW_ONE_KEY_DEPLOY:
			// 一键部署
			switch (nextStep) {
			case STEP_VALID_WITH_MD5_FILE:
				// 校验md5文件列表
				ClientFileManager.checkProjectFiles(flowName, version);
				break;
			case STEP_KILL_PROCESS:
				ProcessUtil.killByPort(SocketClient.pidPort);
				break;
			case STEP_START_PROCESS:
				commandThread.startProcess();
				break;
			}
			break;
		}
	}
}
