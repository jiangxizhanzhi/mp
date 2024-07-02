package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileUtils
 * @Description 文件工具类
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Component
@Slf4j
public class FileUtils {

    /**
     * 获取文件后缀
     *
     * @param filename
     * @return
     */
    public static String getSuffix(String filename) {
        String suffix = StringUtils.EMPTY;
        if (StringUtils.isBlank(filename)) {
            return suffix;
        }

        Path path = Path.of(filename);
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(AuthConstant.SEPARATOR_CHAR_DOT);
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            suffix = fileName.substring(dotIndex + 1);
        }

        return suffix;
    }

    /**
     * 获取文件类型
     *
     * @param inputStream
     * @return
     */
    public static String getType(InputStream inputStream) {
        String fileType = null;
        byte[] fileHeader = new byte[8]; // 读取文件开头的8个字节
        int bytesRead = 0;
        try {
            bytesRead = inputStream.read(fileHeader);
        } catch (Exception e) {
            log.error("Failed to get file type", e);
            return fileType;
        }

        if (bytesRead == fileHeader.length) {
            fileType = determineFileType(fileHeader);
            log.info("File type: " + fileType);
        } else {
            log.info("Unable to determine file type.");
        }

        return fileType;
    }

    /**
     * 写入文件
     *
     * @param content
     * @param file
     * @param charset
     * @throws IOException
     */
    public static void writeString(String content, File file, Charset charset) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] contentBytes = content.getBytes(charset);
            fos.write(contentBytes);
        } catch (IOException e) {
            log.error("write to file error:", e);
        }
    }

    /**
     * 逐行读取文件
     *
     * @param file
     * @return
     */
    public static List<String> readUtf8Lines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.error("read lines from file error:", e);
        }
        return lines;
    }

    /**
     * 解析文件类型
     *
     * @param fileHeader
     * @return
     */
    private static String determineFileType(byte[] fileHeader) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : fileHeader) {
            hexString.append(String.format("%02X", b));
        }

        String hex = hexString.toString();
        for (Map.Entry<String, String> fileTypeEntry : FileTypeUtils.FILE_TYPE_MAP.entrySet()) {
            if (hex.toLowerCase().startsWith(fileTypeEntry.getKey())) {
                return fileTypeEntry.getValue();
            }
        }

        return null;
    }

}