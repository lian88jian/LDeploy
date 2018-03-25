package per.lian.deploy.server;

import com.alibaba.fastjson.JSONObject;

public class ClientInfo {
	
	private String type;
	
	private ClientThread clientThread;

	private String os;

	public ClientInfo(JSONObject clientJson) {
		
		setType(clientJson.getString("type"));
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
}
