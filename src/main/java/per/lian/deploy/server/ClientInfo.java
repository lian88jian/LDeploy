package per.lian.deploy.server;

import com.alibaba.fastjson.JSONObject;

public class ClientInfo {
	
	private String type;
	
	private ClientThread clientThread;

	private String os;
	
	private String clientName;
	
	/**
	 * 使用服务端配置文件初始化
	 * @param clientJson
	 */
	public ClientInfo(JSONObject clientJson) {
		
		setType(clientJson.getString("type"));
		setClientName(clientJson.getString("name"));
	}

	public ClientThread getClientThread() {
		return clientThread;
	}

	public void setClientThread(ClientThread clientThread) {
		this.clientThread = clientThread;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
}
