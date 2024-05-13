package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.common.constant.CommonConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class FileUtil {

	private FileUtil() {}

	/**
	 * 校验允许的类型
	 * @param file
	 * @param fileSuffix
	 */
	public static void validateSuffix(MultipartFile file, String... fileSuffix) {
		if (null == file) {
			log.error("file is null! fileSuffix=[{}]",fileSuffix);
			throw new MpRuntimeException(CsceneErrorCode.FILE_CONTENT_INVALID);
		}

		String filename = file.getOriginalFilename();
		filename = filename.toLowerCase();
		for (String suffix : fileSuffix) {
			if (filename.endsWith(CommonConstant.PERIOD + suffix)) {
				return;
			}
		}
		log.error("filename is illegal! filename=[{}]",filename);
		throw new MpRuntimeException(CsceneErrorCode.FILE_TYPE_ERROR);
	}

	/**
	 * 校验不允许的类型
	 * @param file
	 * @param fileSuffix
	 */
	public static void validateNotAccessFile(MultipartFile file, String... fileSuffix) {
		if (null == file) {
			log.error("file is null! fileSuffix=[{}]",fileSuffix);
			throw new MpRuntimeException(CsceneErrorCode.FILE_CONTENT_INVALID);
		}

		String filename = file.getOriginalFilename();
		for (String suffix : fileSuffix) {
			if (filename.endsWith(CommonConstant.PERIOD + suffix)) {
				log.error("filename is illegal! filename=[{}]",filename);
				throw new MpRuntimeException(CsceneErrorCode.FILE_TYPE_ERROR);
			}
		}
	}

	public static File saveFile(MultipartFile file, String basePath) throws IOException {
		String originalFilename = file.getOriginalFilename();
		assert originalFilename != null;
		String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uuid = String.valueOf(UUID.randomUUID());
		String fileName = uuid + fileSuffix;

		mkdirs(basePath);
		File newFile = new File(basePath, fileName);
		file.transferTo(newFile);
		return newFile;
	}

	public static File mkdirs(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取文件名称
	 * @param filePath 文件路径
	 * @param suffix 是否包含后缀
	 * @return
	 */
	public static String getFileName(String filePath, Boolean suffix) {

		File file = new File(filePath);

		// 文件加返回名称
		if (file.isDirectory()) {
			return file.getName();
		}

		// 无后缀名称直接返回文件名
		if (file.getName().lastIndexOf(CommonConstant.PERIOD) == -1) {
			return file.getName();
		}

		return !suffix
				? file.getName().substring(CommonConstant.ZERO, file.getName().lastIndexOf("."))
				: file.getName();
	}

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
			throw new MpRuntimeException(CsceneErrorCode.FILE_CONTENT_INVALID);
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

	/**
	 * 以行读取文件 返回行数据列表
	 * @param file
	 * @return
	 */
	public static List<String> listContent(File file) {
		BufferedReader br = null;
		List<String> contentList = Lists.newArrayList();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

			String content;
			while ((content = br.readLine()) != null) {
				contentList.add(content);
			}

			return contentList;
		} catch (Exception e) {
			// 文件读取失败
			log.error("file read failed! filePath={} error:", file.getAbsolutePath(), e);
			throw new MpRuntimeException(CsceneErrorCode.FILE_READ_FAIL, file.getPath());
		} finally {
			try {
				if (br!=null) {
					br.close();
				}
			} catch (IOException e) {
				// 不做任何处理
			}
		}
	}

	/**
	 * 文件拷贝
	 * @param path
	 * @param dest
	 * @param rename true=已存在修改名称 false已存在则覆盖
	 */
	public static String copyFile(String path, String dest, boolean rename) {

		File destFile = new File(dest);

		// 文件重命名
		if (rename && destFile.exists()) {
			dest = reName(dest);
		}

		// 父级文件目录创建
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}

		// 文件拷贝
		FileChannel input = null;
		FileChannel output = null;
		try {
			input = new FileInputStream(path).getChannel();
			output = new FileOutputStream(dest).getChannel();

			output.transferFrom(input, 0, input.size());
		} catch (Exception e) {
			log.error("copyFile: File copy failed! path=[{}], dest=[{}], catch error:", path, dest,
					e);
			throw new MpRuntimeException(CsceneErrorCode.FILE_COPY_FAIL);
		} finally {
			try {
				if (null != input) {
					input.close();
				}
				if (null != output) {
					output.close();
				}
			} catch (Exception e) {
				// 不管
			}

		}

		return dest;
	}

	/**
	 * 将字符串写入文件
	 * @param data 要写入的字符串
	 * @param osw 输出流
	 * @throws IOException
	 */
	public static void writeString(String data, OutputStreamWriter osw) throws IOException {
		try {
			osw.write(data);
		} catch (Exception e) {
			log.error("error occurred when writing data.json", e);
			throw new IOException(e);
		} finally {
			try {
				osw.flush();
				osw.close();
			} catch (IOException e) {
				log.error("failed to close OutputStreamWriter", e);
			}
		}
	}

	/**
	 * 文件写到目的文件夹
	 * @param data
	 * @param destFilePath
	 */
	public static void writeToDestFile(String data, String destFilePath) {

		File file = new File(destFilePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(destFilePath), "UTF-8"));
			out.write(data);
		} catch (Exception e) {
			log.error("File write failed! dest_file_path=[{}], catch error:", destFilePath, e);
			throw new MpRuntimeException(CsceneErrorCode.FAILED_TO_WRITE_FILE);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (Exception e) {
					// 关不掉算了
				}
			}
		}
	}

	/**
	 * @Author 陈卓
	 * 生成文件响应
	 * @param file 返回的文件
	 * @param delete 是否删除本地文件
	 * @param response 响应
	 */
	public static void setResponse(File file, Boolean delete, HttpServletResponse response) {
		// 以流的形式下载文件。
		InputStream fis = null;
		OutputStream outputStream = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);

			// 清空response
			response.reset();

			// 设置返回
			response.setContentType("application/octet-stream;charset=UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8").replaceAll("\\+","%20"));
			outputStream = response.getOutputStream();
			outputStream.write(buffer);
			outputStream.flush();
		} catch (IOException e) {
			log.error("setResponse error:", e);
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				log.error("setResponse error:", e);
			}

			if (delete.equals(Boolean.TRUE) && null != file && file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * 递归删除文件夹
	 * @param dir 要删除的文件夹
	 * @return true: 删除成功； false: 删除失败
	 */
	public static Boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			assert children != null;
			for (File file : children) {
				if (!deleteDir(file)) {
					log.error("failed to delete file {}", file);
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 文件目录拷贝 如果存在则覆盖
	 * @param dir
	 * @param destDir
	 * @param self true=目录拷贝进目标文件夹 false=把目录下文件拷贝进目标文件夹
	 */
	public static void copyDirectory(String dir, String destDir, Boolean self) {

		// 不存在不做任何操作
		File originFile = new File(dir);
		if (!originFile.exists()) {
			return;
		}

		File destFile = new File(destDir);
		if (!destFile.exists()) {
			destFile.mkdirs();
		}

		String selfPre = originFile.getParentFile().getAbsolutePath();
		String noSelfPre = originFile.getAbsolutePath();

		// 拷贝目录 文件夹则创建对应文件夹 文件则拷贝文件
		List<String> childrenFile = listDirs(dir, true);
		for (String direction : childrenFile) {
			File file = new File(direction);

			// 过滤不必要的文件夹 隐藏文件夹
			if (file.getName().startsWith(".")) {
				continue;
			}
			// 创建对应目录
			if (file.isDirectory()) {
				String pathName = self ? file.getAbsolutePath().replace(selfPre, "")
						: file.getAbsolutePath().replace(noSelfPre, "");
				File copyDir = new File(destDir, pathName);
				copyDir.mkdirs();
				copyDirectory(file.getAbsolutePath(), copyDir.getAbsolutePath(), false);
			}

			// 拷贝对应文件
			if (file.isFile()) {
				String pathName = self ? file.getAbsolutePath().replace(selfPre, "")
						: file.getAbsolutePath().replace(noSelfPre, "");
				copyFile(file.getAbsolutePath(), new File(destDir, pathName).getAbsolutePath(),
						false);
			}
		}
	}

	/**
	 * 根据传入的根目录 获取对应的目录名称的文件路径列表
	 * @param rootFile
	 * @param depth 深度 无限往下则为null
	 * @param includeFile 是否包含文件 true包含 false不包含
	 * @return
	 */
	public static List<String> listFileByFile(File rootFile, Integer depth, Boolean includeFile) {

		// 文件校验返回
		if (!rootFile.exists()) {
			return Lists.newArrayList();
		}

		// 是包含文件过滤
		if (!includeFile && rootFile.isFile()) {
			return Lists.newArrayList();
		}

		// 根据审读计算是否继续往下
		if (depth != null && depth > 0) {
			depth--;
		} else if (depth != null && depth <= 0) {
			return Lists.newArrayList();
		}

		// 遍历文件返回
		File[] files = rootFile.listFiles();
		List<String> allDir = Lists.newArrayList();
		for (File file : files) {
			allDir.add(file.getAbsolutePath());
			List<String> childrenDirs = listFileByFile(file, depth, includeFile);
			allDir.addAll(childrenDirs);
		}

		return allDir;
	}

	/**
	 * 获取目录下的文件夹
	 *
	 * @param path
	 * @param includeFile true 包含文件 false不包含文件
	 * @return
	 */
	public static List<String> listDirs(String path, boolean includeFile) {

		File rootFile = new File(path);
		if (!rootFile.exists()) {
			log.error("File not exist! path=[{}]", path);
			return Lists.newArrayList();
		}
		File[] files = rootFile.listFiles();

		List<String> directions = Arrays.stream(files)
				.filter(file -> !file.getName().startsWith("."))
				.filter(file -> includeFile ? true : file.isDirectory()).map(File::getAbsolutePath)
				.sorted().collect(Collectors.toList());
		return directions;
	}

	/**
	 * 获取目录下的文件夹
	 *
	 * @param path
	 * @param includeDir true 包含文件夹 false不包含文件夹
	 * @return
	 */
	public static List<String> listFileNamesByDir(String path, boolean includeDir) {

		File rootFile = new File(path);
		if (!rootFile.exists()) {
			log.error("File not exist! path=[{}]", path);
			return Lists.newArrayList();
		}
		File[] files = rootFile.listFiles();

		List<String> directions = Arrays.stream(files)
				.filter(file -> !file.getName().startsWith("."))
				.filter(file -> includeDir ? true : file.isFile()).map(File::getName).sorted()
				.collect(Collectors.toList());
		return directions;
	}

	/**
	 * 获取文件夹下的一个文件
	 * @param path
	 * @param fileSuffix
	 * @return
	 */
	public static String getOneFile(String path, String fileSuffix) {
		File file = new File(path);
		if (file.isFile()) {
			return "";
		}

		List<String> files = listDirs(file.getAbsolutePath(), true);
		for (String dir : files) {
			File underFile = new File(dir);

			if (underFile.isFile() && StringUtils.isBlank(fileSuffix)) {
				// 无后缀直接返回文件地址
				return dir;
			}

			if (underFile.isFile() && StringUtils.isNotBlank(fileSuffix)
					&& underFile.getName().endsWith(fileSuffix)) {
				// 有后缀则进行过滤
				return dir;
			}
		}
		return "";
	}

	/**
	 * 文件重命名
	 * @param dest
	 * @return
	 */
	private static String reName(String dest) {
		String suffix = dest.substring(dest.lastIndexOf("."));
		String filePath = dest.substring(0, dest.lastIndexOf("."));

		return filePath + IDGeneratorUtil.getID() + suffix;
	}

	/**
	 * 获取新文件名且不重复 可传入文件路径和名称
	 * @return
	 */
	public static String getNewName(String nameOrPath) {

		// 文件名处理
		int suffixIndex = nameOrPath.lastIndexOf(CommonConstant.PERIOD);
		if (suffixIndex == CommonConstant.NEGATIVE_ONE) {
			return nameOrPath;
		}
		String fileName = IDGeneratorUtil.getID() + nameOrPath.substring(suffixIndex);

		// 文件路径处理
		int dirIndex = nameOrPath.lastIndexOf(CommonConstant.LEFT_OBLIQUE_LINE);
		if (dirIndex == CommonConstant.NEGATIVE_ONE) {
			return fileName;
		}
		String filePath = nameOrPath.substring(0, dirIndex + 1);

		return filePath + fileName;
	}

	/**
	 * 校验文件后缀名
	 * @param destFilePath 目标文件地址 文件名称和绝对路径均可以
	 * @param fileTypeStr 文件后缀字符串 可参考FileTypeContant传入数据
	 * @return
	 */
	public static void validateFileSuffix(String destFilePath, String fileTypeStr) {

		if (StringUtils.isBlank(destFilePath)) {
			log.error("destFilePath is bank!");
			throw new MpRuntimeException(CsceneErrorCode.FILE_CONTENT_INVALID);
		}

		// 文件后缀匹配，如果文件无后缀则可能不是文件
		String fileSuffix = getFileSuffix(destFilePath);

		// 文件类型为空 则文件无后缀
		if (StringUtils.isBlank(fileTypeStr)) {
			if (StringUtils.isBlank(fileSuffix)) {
				return;
			} else {
				log.error("fileSuffix is not  bank! fileSuffix = [{}]",fileSuffix);
				throw new MpRuntimeException(CsceneErrorCode.NOT_SUPPORT_FILE_TYPE,
						fileTypeStr.toLowerCase());
			}
		}

		// 如果类型不为空 但后缀为空 则校验不通过
		if (StringUtils.isBlank(fileSuffix)) {
			log.error("fileSuffix is  bank! fileTypeStr = [{}]",fileTypeStr);
			throw new MpRuntimeException(CsceneErrorCode.NOT_SUPPORT_FILE_TYPE, fileTypeStr.toLowerCase());
		}

		String suffixStr = fileSuffix.toUpperCase();
		List<String> typeList = Arrays.asList(fileTypeStr.split(CommonConstant.CODE_COMMA));

		if (!typeList.contains(suffixStr)) {
			log.error("File type error! file_path=[{}], suffix=[{}], file_type_str=[{}]",
					destFilePath, suffixStr, fileTypeStr);
			throw new MpRuntimeException(CsceneErrorCode.NOT_SUPPORT_FILE_TYPE, fileTypeStr.toLowerCase());
		}

	}

	/**
	 * 获取文件后缀名称
	 * @param filePath
	 * @return
	 */
	public static String getFileSuffix(String filePath) {
		int indexOf = filePath.lastIndexOf(CommonConstant.PERIOD);
		if (indexOf == -1) {
			return "";
		}

		return filePath.substring(indexOf + 1);
	}

	/**
	 * 文件夹目录删除
	 * @param destDir
	 * @param self
	 */
	public static void deleteDir(String destDir, boolean self) {
		File file = new File(destDir);
		if (!file.exists()) {
			return;
		}
		
		try {
			if (file.isFile()) {
				FileUtils.delete(file);
				return;
			}
			
			if (self) {
				FileUtils.deleteDirectory(file);
				return;
			}

			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				return;
			}

			for (File subFile : files) {
				if (subFile.isFile()) {
					subFile.delete();
				} else {
					FileUtils.deleteDirectory(subFile);
				}
 			}
		} catch (Exception e) {
			log.error("Directory delete failed! file_path=[{}], catch error:", destDir, e);
			throw new MpRuntimeException(CsceneErrorCode.FILE_DELETE_FAILED);
		}
		
	}

	public static String getHash256(File file) throws IOException, NoSuchAlgorithmException {
		String value = "";
		try (InputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			int numRead;
			// 返回实现指定摘要算法的 MessageDigest 对象
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					// 更新摘要
					digest.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			value = new String(
					org.apache.tomcat.util.codec.binary.Base64.encodeBase64(digest.digest()));
		}
		return value;
	}

	/**
	 * 创建父级目录
	 * @param filePath
	 */
	public static void createParentPath(String filePath) {

		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
	}

	public static void copyFile(String originFile, String destFile) {
		copyFile(new File(originFile), new File(destFile));
	}

	public static void copyFile(File file, File destFile) {

		try {
			FileUtils.copyFile(file, destFile);
		} catch (Exception e) {
			log.error("File copy failed! origin_path=[{}], dest_path=[{}], catch error:",
					file.getAbsolutePath(), destFile.getAbsolutePath(), e);
			throw new MpRuntimeException(CsceneErrorCode.FILE_COPY_FAIL);
		}
	}

	public static void createPath(File file) {

		if (file.exists()) {
			return;
		}

		file.mkdirs();
	}

	public static void createManyPath(String parentPath, String... children) {
		for (String child : children) {
			createPath(new File(parentPath, child));
		}
	}
}
