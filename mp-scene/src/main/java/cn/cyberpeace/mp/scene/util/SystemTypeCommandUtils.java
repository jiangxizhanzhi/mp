package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SystemTypeCommandUtils {

    public static final String OS_TYPE_WINDOWS = "windows";

    public static final String OS_TYPE_LINUX = "linux";

    private SystemTypeCommandUtils() {}

    public static final Map<String, String> SYSTEM_TYPE_OS_TYPE = new HashMap<>() {
        {
            put(BaseSceneElementModel.SystemType.WINDOWS_10, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_8, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_7, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_XP, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_VISTA, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2019, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2016, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2012, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2010, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2008, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2003, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.WINDOWS_SERVER_2000, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.CENTOS, OS_TYPE_LINUX);
            put(BaseSceneElementModel.SystemType.UBUNTU, OS_TYPE_LINUX);
            put(BaseSceneElementModel.SystemType.KALI, OS_TYPE_LINUX);
            put(BaseSceneElementModel.SystemType.OTHER, OS_TYPE_LINUX);

            put(BaseSceneElementModel.SystemType.WINDOWS, OS_TYPE_WINDOWS);
            put(BaseSceneElementModel.SystemType.LINUX, OS_TYPE_LINUX);
        }
    };

    public static String getOsType(String systemType) {
        return SYSTEM_TYPE_OS_TYPE.getOrDefault(systemType.toLowerCase(), OS_TYPE_LINUX);
    }

    public static boolean isWindows(String systemType) {
        return OS_TYPE_WINDOWS.equals(getOsType(systemType));
    }

    public static boolean isLinux(String systemType) {
        return OS_TYPE_LINUX.equals(getOsType(systemType));
    }

    public static String getLineBreak(String systemType) {
        var osType = getOsType(systemType);
        if (OS_TYPE_WINDOWS.equals(osType)) {
            return CommandUtils.WINDOWS_LINE_BREAK;
        } else if (OS_TYPE_LINUX.equals(osType)) {
            return CommandUtils.LINUX_LINE_BREAK;
        } else {
            return CommandUtils.LINUX_LINE_BREAK;
        }
    }

    public static String getPathDelimiter(String systemType) {
        var osType = getOsType(systemType);
        if (OS_TYPE_WINDOWS.equals(osType)) {
            return CommandUtils.WINDOWS_PATH_DELIMITER;
        } else if (OS_TYPE_LINUX.equals(osType)) {
            return CommandUtils.LINUX_PATH_DELIMITER;
        } else {
            return CommandUtils.LINUX_PATH_DELIMITER;
        }
    }

    public static String joinCommands(String systemType, String... commands) {
        return StringUtils.joinWith(getLineBreak(systemType), commands);
    }

    public static String appendBeforeCommand(String command, String beforeCommand, String systemType) {
        return StringUtils.joinWith(getLineBreak(systemType), beforeCommand, command);
    }

    public static String appendAfterCommand(String command, String afterCommand, String systemType) {
        return StringUtils.joinWith(getLineBreak(systemType), command, afterCommand);
    }

    public static String joinPath(String systemType, String... paths) {
        return StringUtils.joinWith(getPathDelimiter(systemType), paths);
    }

    public static String doCommandInDir(String systemType, String dir, String command) {
        var osType = getOsType(systemType);
        if (OS_TYPE_WINDOWS.equals(osType)) {
            return CommandUtils.windowsDoCommandInDir(dir, command);
        } else if (OS_TYPE_LINUX.equals(osType)) {
            return CommandUtils.linuxDoCommandInDir(dir, command);
        } else {
            return command;
        }
    }
}
