package cn.cyberpeace.mp.behavior.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.UUID;

public class ZipUtil {

	public static String generatePath() {
		String tmpPath;
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			tmpPath = "/C:/test/";
		} else {
			tmpPath = "/tmp/";
		}
		File file = FileUtil.mkdirs(tmpPath + UUID.randomUUID());
		return file.getPath();
	}

	public static String unZipToTmpPath(String zipPath) throws ZipException {
		ZipFile zipFile = new ZipFile(zipPath);
		String tmpPath = generatePath();
		zipFile.extractAll(tmpPath);
		return tmpPath;
	}
}
