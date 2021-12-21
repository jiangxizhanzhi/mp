package cn.cyberpeace.mp.scene.util;

public class SceneCommandUtils {

    public static final String ORIGIN_COMMAND_PREFIX = "o:";

    public static final String SCENE_COMMAND_PREFIX = "s:";

    public static final String NODE_COMMAND_PREFIX = "n:";

    public static final String WINDOWS_CALL_TEMPLATE = "call %s";

    /**
     * 解析场景命令
     * @param command
     *        无特殊前缀-普通命令
     *        `o:`前缀-普通命令
     *        `s:`前缀-场景文件命令
     *        `n:`前缀-节点文件命令
     * @return parsed command
     */
    public static String parseCommand(String command, String sceneId, String elementId, String systemType) {
        String parsedCommand;
        boolean isWindows = SystemTypeCommandUtils.isWindows(systemType);
        if (command.startsWith(ORIGIN_COMMAND_PREFIX)) {
            parsedCommand = command.substring(ORIGIN_COMMAND_PREFIX.length());
        } else if (command.startsWith(SCENE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(SCENE_COMMAND_PREFIX.length());
            var dir = SceneFileUtils.getSceneFileUnzipDir(systemType, sceneId);
            parsedCommand = SystemTypeCommandUtils.doCommandInDir(systemType, dir, relativeCommand);
            // 确保call执行不退出
            if (isWindows) {
                parsedCommand = String.format(WINDOWS_CALL_TEMPLATE, parsedCommand);
            }
        } else if (command.startsWith(NODE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(NODE_COMMAND_PREFIX.length());
            var dir = SceneFileUtils.getElementFileUnzipDir(systemType, sceneId, elementId);
            parsedCommand = SystemTypeCommandUtils.doCommandInDir(systemType, dir, relativeCommand);
            // 确保call执行不退出
            if (isWindows) {
                parsedCommand = String.format(WINDOWS_CALL_TEMPLATE, parsedCommand);
            }
        } else {
            parsedCommand = command;
        }

        return parsedCommand;
    }

}
