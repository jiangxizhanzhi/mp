package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CommandUtils {

    public static final String WINDOWS_LINE_BREAK = "\r\n";
    public static final String LINUX_LINE_BREAK = "\n";

    public static final String OS_TYPE_WINDOWS = "windows";
    public static final String OS_TYPE_LINUX = "linux";

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

    private CommandUtils() {}

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
            return WINDOWS_LINE_BREAK;
        } else if (OS_TYPE_LINUX.equals(osType)) {
            return LINUX_LINE_BREAK;
        } else {
            return LINUX_LINE_BREAK;
        }
    }

    /**
     * 修复带中文的windows命令
     * @param command windows命令
     * @return 修复命令
     */
    public static String fixWinZhCommand(String command) {
        return StringUtils.joinWith(WINDOWS_LINE_BREAK, "chcp 65001", command, "chcp 936");
    }

    public static String joinCommands(String[] commands, String systemType) {
        return StringUtils.joinWith(getLineBreak(systemType), commands);
    }

    public static String appendBeforeCommand(String command, String beforeCommand, String systemType) {
        return StringUtils.joinWith(getLineBreak(systemType), beforeCommand, command);
    }

    public static String appendAfterCommand(String command, String afterCommand, String systemType) {
        return StringUtils.joinWith(getLineBreak(systemType), command, afterCommand);
    }

}
