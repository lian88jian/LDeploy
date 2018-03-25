package per.lian.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Platform;

import per.lian.jna.Kernel32;

public class ProcessUtil {

	public static long getPidByJni(Process process) {
		long pid = -1;
		Field field = null;
		try {
			if (Platform.isWindows()) {

				field = process.getClass().getDeclaredField("handle");
				field.setAccessible(true);
				pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
			} else if (Platform.isLinux() || Platform.isAIX()) {

				Class<?> clazz = Class.forName("java.lang.UNIXProcess");
				field = clazz.getDeclaredField("pid");
				field.setAccessible(true);
				pid = (Integer) field.get(process);
			} else {

				throw new RuntimeException("unknow Platform for get pid");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return pid;
	}

	public static void killProcessByPid(long pid) {

		if (Platform.isWindows()) {
			execute("taskkill /pid " + pid + " -t -f");
		} else {
			execute("kill -9 " + pid);
		}
	}

	public static long findPidByPort(int port) {

		if (Platform.isWindows()) {

			// netstat -ano | findstr /c:LISTENING | findstr /c:" 0.0.0.0:8080 "
			String result = execute("cmd.exe /c netstat -ano | findstr /c:LISTENING | findstr /c:\" 0.0.0.0:" + port + " \"");
			if (StringUtils.isEmpty(result)) {
				return -1;
			}
			String[] ss = result.replaceAll("[\\s]+", " ").split(" ");
			return Long.parseLong(ss[ss.length - 1]);
		} else {
			throw new RuntimeException("unspported platform for method ProcessUtil.findPidByPort");
		}
	}

	public static void killByPid(long pid) {

		if (Platform.isWindows()) {
			
			execute("cmd.exe /c taskkill /pid " + pid + " -t -f");
		} else if (Platform.isLinux()) {
			
			execute("kill -9 " + pid);
		} else {
			throw new RuntimeException("unspported platform for method ProcessUtil.findPidByPort");
		}
	}

	public static void main(String[] args) {

		long pid = findPidByPort(8080);
		killByPid(pid);
	}

	public static String execute(String cmd) {

		Runtime runtime = Runtime.getRuntime();
		StringBuffer buff = new StringBuffer();
		try {
			Process process = runtime.exec(cmd);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String result = null;

			while ((result = reader.readLine()) != null) {
				buff.append(result + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buff.toString();
	}
}
