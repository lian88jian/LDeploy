package per.lian.deploy.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.util.DigestUtils;

import per.lian.utils.FileUtil;

public class ServerFileManager {

	/**
	 * 生成文件md5列表
	 */
	public static void generaterMd5File(String serverType, String serverVersion) throws Exception {

		String serverDirPath = SocketServer.WorkDir + "\\" + serverType + "\\" + serverVersion + "\\";
		File md5File = new File(serverDirPath + "md5.txt");
		if (md5File.exists()) {
			return;
		}
		
		StringBuffer buff = new StringBuffer();
		List<File> fileList = FileUtil.getFilesByExt(new File(serverDirPath), null);
		for (File _file : fileList) {
			
			String _md5 = DigestUtils.md5DigestAsHex(new FileInputStream(_file));
			buff.append(_file.getAbsolutePath().replace(serverDirPath, "") + ":" + _md5 + "\r\n");
		}
		FileUtil.createFileWithBytes(serverDirPath + "md5.txt", buff.toString().getBytes());
	}
}
