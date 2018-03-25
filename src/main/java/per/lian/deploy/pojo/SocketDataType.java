package per.lian.deploy.pojo;

public interface SocketDataType {
	
	/**
	 * 客户端身份信息
	 */
	public static final int CLIENT_INFO = 101;
	/**
	 * 客户端控制台信息
	 */
	public static final int CLIENT_CMD_MSG = 100;
	/**
	 * 客户端心跳
	 */
	public static final int CLIENT_CMD_HEART = 102;
	/**
	 * 服务器要求客户端执行的cmd命令
	 */
	public static final int SERVER_CMD = 200;
	
	/**
	 * 服务器发送文件
	 */
	public static final int SERVER_FILE = 201;
	
	/**
	 * 服务器发送关闭指令
	 */
	public static final int SERVER_SHUTDOWN = 202;
	
	/**
	 * 一键部署
	 */
	public static final int SERVER_ONEKEY_DEPLOY = 203;
}
