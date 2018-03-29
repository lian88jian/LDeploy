package per.lian.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件操作类
 * 
 * @author lian
 * @date 2016年5月10日
 */
public class FileUtil {

	private static String absolutePath = "";

	private static Log logger = LogFactory.getLog(FileUtil.class);

	/**
	 * 获取类包的绝对路径
	 * 
	 * @return
	 */
	public static String getAbsolutePath() {
		if (absolutePath.length() == 0) {
			String path = null;
			try {
				URL url = FileUtil.class.getResource("/");
				if (url == null)
					url = ClassLoader.getSystemResource("");
				path = url.getPath();
			} catch (Exception e) {
				File directory = new File("");
				path = directory.getPath();
			}
			absolutePath = (path.replace("/build/classes", "").replace("%20", " ").replace("target/test-classes/",
					"target/classes/")).replaceFirst("/", "");
		}
		return absolutePath;
	}

	public static String readFileContent(InputStream inputStream, String encoding) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStreamReader read = new InputStreamReader(inputStream, encoding);// 考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				sb.append(lineTxt + "\n");
			}
			read.close();
		} catch (Exception e) {
			logger.error("读取文件内容出错", e);
		}
		return sb.toString();
	}

	public static String readFileContent(File file, String encoding) {
		StringBuffer sb = new StringBuffer();
		try {
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt + "\n");
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			logger.error("读取文件内容出错", e);
		}
		return sb.toString();
	}

	/**
	 * 把一个文件转化为字节
	 * 
	 * @param file
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getBytes(File file) {
		byte[] bytes = null;
		if (file != null) {
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				int length = (int) file.length();
				bytes = new byte[length];
				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
				// 如果得到的字节长度和file实际的长度不一致就可能出错了
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				IOUtil.close(is);
			}
		}
		return bytes;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static File createFileWithBytes(String filePath, String fileName, byte[] buff) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = new File(filePath + "\\" + fileName);
		try {
			File dir = file.getParentFile();
			if (!dir.exists() || !dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buff);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static File createFileWithBytes(String filePathAndName, byte[] buff) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(filePathAndName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buff);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 获取目录下，特定格式文件
	 * 
	 * @return
	 */
	public static List<File> getFilesByExt(File dir, String ext) {
		List<File> list = new ArrayList<File>();
		List<File> list_bk = new ArrayList<File>();
		popAllFile(list, dir);
		for (File temp : list) {
			int dot = temp.getName().lastIndexOf('.') + 1;
			String _ext = temp.getName().substring(dot);

			if (ext == null || ext.equals(_ext))
				list_bk.add(temp);
		}
		return list_bk;
	}

	/**
	 * 递归查找
	 */
	private static void popAllFile(List<File> list, File file) {
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			for (File temp : childFile) {
				popAllFile(list, temp);
			}
		} else if (file.isFile()) {
			list.add(file);
		}
	}

	/**
	 * 根据文件名获取文件
	 * 
	 * @return 获取文件
	 */
	public static File getFile(String filename) {
		File file = null;

		file = new File(filename);

		return file;
	}

	/**
	 * 使用文件通道的方式复制文件
	 * 
	 * @param orgFile
	 *            源文件
	 * @param desFile
	 *            复制到的新文件
	 * @throws IOException
	 */

	public static void copyFile(File orgFile, File desFile) throws IOException {

		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(orgFile);
			fo = new FileOutputStream(desFile);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<File> getDirs(File projectDir) {
		if(!projectDir.exists()) {
			return new ArrayList<File>();
		}
		File[] files = projectDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File _file) {
				return _file.isDirectory();
			}
		});
		return Arrays.asList(files);
	}

}
