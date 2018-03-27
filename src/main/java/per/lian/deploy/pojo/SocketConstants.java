package per.lian.deploy.pojo;

public interface SocketConstants {
	
	public static final String SP = "/";
	/**
	 * 一键部署流程
	 */
	public static final String FLOW_ONE_KEY_DEPLOY = "FLOW_ONE_KEY_DEPLOY";
	/**
	 * 校验md5文件列表
	 */
	public static final String STEP_VALID_WITH_MD5_FILE = "STEP_VALID_WITH_MD5_FILE";
	/**
	 * 杀死进程
	 */
	public static final String STEP_KILL_PROCESS = "STEP_KILL_PROCESS";
	/**
	 * 启动进程
	 */
	public static final String STEP_START_PROCESS = "STEP_START_PROCESS";
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
	 * 客户端错误
	 */
	public static final int CLIENT_ERROR = 103;
	/**
	 * 客户端请求文件
	 */
	public static final int CLIENT_REQUIRE_FILE = 104;
	/**
	 * 客户端请求文件
	 */
	public static final int CLIENT_REQUIRE_MD5 = 105;
	/**
	 * 客户端请求服务器发送下一步指令
	 */
	public static final int CLIENT_NEXT_STEP = 106;
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
	/**
	 * 服务器发送下一步指令
	 */
	public static final int SERVER_NEXT_STEP = 204;
}
