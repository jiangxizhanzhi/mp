package cn.cyberpeace.mp.behavior.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class FileUtil {

	/**
	 * 读取文件内容
	 * @param file
	 * @return
	 */
	public static String getContent(File file) {
		BufferedReader br = null;
		StringBuilder builder = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			String content = "";
			while ((content = br.readLine()) != null) {
				builder.append(content + System.lineSeparator());
			}

			return builder.toString();
		} catch (Exception e) {
			// 文件读取失败
			log.error("file read failed! filePath={} error:", file.getAbsolutePath(), e);
			throw new MpRuntimeException("文件读取失败");
		} finally {
			try {
				if (null != br) {
					br.close();
				}
			} catch (IOException e) {
				// 不做任何处理
			} catch (NullPointerException e) {
				// 对象未创建空指针异常捕获，不做任何处理
			}
		}
	}

	public static File mkdirs(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}
