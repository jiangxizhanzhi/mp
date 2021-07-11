package cn.cyberpeace.mp.scene.util;

import org.apache.commons.lang3.StringUtils;

public class CommandUtils {

    public static final String WINDOWS_LINE_BREAK = "\r\n";
    public static final String LINUX_LINE_BREAK = "\n";

    public static final String WINDOWS_PATH_DELIMITER = "\\";
    public static final String LINUX_PATH_DELIMITER = "/";

    
    public static final String WINDOWS_MK_DIR = "md %s";

    
    public static final String WINDOWS_CD_DIR = "cd %s";

    
    public static final String WINDOWS_CALL_TARGET = "call \"%s\"";

    
    public static final String WINDOWS_START_TARGET = "start /b %s";

    
    public static final String WINDOWS_DOWNLOAD_FILE = "curl -o \"%s\" %s";

    
    public static final String WINDOWS_UNZIP_FILE = "unzip -o \"%s\" -d \"%s\"";

    
    public static final String LINUX_MK_DIR = "mkdir -p %s";

    
    public static final String LINUX_CD_DIR = "cd %s";

    
    public static final String LINUX_CHMOD_X = "chmod -R +x %s";

    
    public static final String LINUX_CALL_SH = "/bin/sh %s";

    
    public static final String LINUX_DOWNLOAD_FILE = "curl -o \"%s\" %s";

    
    public static final String LINUX_UNZIP_FILE = "unzip -o %s -d %s";

    private CommandUtils() {}

    
    public static String fixWinZhCommand(String command) {
        return StringUtils.joinWith(WINDOWS_LINE_BREAK, "chcp 65001", command, "chcp 936");
    }

    public static String windowsMkDir(String dir) {
        return String.format(WINDOWS_MK_DIR, dir);
    }

    public static String windowsCdDir(String dir) {
        return String.format(WINDOWS_CD_DIR, dir);
    }

    public static String windowsCallTarget(String target) {
        return String.format(WINDOWS_CALL_TARGET, target);
    }

    public static String windowsStartTarget(String target) {
        return String.format(WINDOWS_START_TARGET, target);
    }

    public static String windowsDownloadFile(String fileUrl, String toPath) {
        return String.format(WINDOWS_DOWNLOAD_FILE, toPath, fileUrl);
    }

    public static String windowsUnzipFile(String filePath, String toDir) {
        return String.format(WINDOWS_UNZIP_FILE, filePath, toDir);
    }

    public static String linuxMkDir(String dir) {
        return String.format(LINUX_MK_DIR, dir);
    }

    public static String linuxCdDir(String dir) {
        return String.format(LINUX_CD_DIR, dir);
    }

    public static String linuxChmodX(String filePath) {
        return String.format(LINUX_CHMOD_X, filePath);
    }

    public static String linuxCallSh(String shPath) {
        return String.format(LINUX_CALL_SH, shPath);
    }

    public static String linuxDownloadFile(String fileUrl, String toPath) {
        return String.format(LINUX_DOWNLOAD_FILE, toPath, fileUrl);
    }

    public static String linuxUnzipFile(String filePath, String toDir) {
        return String.format(LINUX_UNZIP_FILE, filePath, toDir);
    }

}
