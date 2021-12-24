package cn.cyberpeace.mp.scene.util;

import java.util.ArrayList;
import java.util.List;

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
     * @return parsed commands
     */
    public static List<String> parseCommand(String command, String sceneId, String elementId, String systemType) {
        List<String> parsedCommands = new ArrayList<>();
        boolean isWindows = SystemTypeCommandUtils.isWindows(systemType);
        if (command.startsWith(ORIGIN_COMMAND_PREFIX)) {
            parsedCommands.add(command.substring(ORIGIN_COMMAND_PREFIX.length()));
        } else if (command.startsWith(SCENE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(SCENE_COMMAND_PREFIX.length());
            var sceneFileDir = SceneFileUtils.getSceneFileUnzipDir(systemType, sceneId);
            // 为兼容在节点目录内执行系统命令，执行相对路径的命令
            if (isWindows) {
                parsedCommands.add(CommandUtils.windowsCdDir(sceneFileDir));
                // 确保call执行不退出
                parsedCommands.add(String.format(WINDOWS_CALL_TEMPLATE, relativeCommand));
            } else {
                parsedCommands.add(CommandUtils.linuxCdDir(sceneFileDir));
                parsedCommands.add(relativeCommand);

                // 对于linux节点文件内命令，相对路径无法执行，再执行一遍绝对路径
                var absoluteCommand = SystemTypeCommandUtils.joinPath(systemType, sceneFileDir, relativeCommand);
                parsedCommands.add(absoluteCommand);
            }
        } else if (command.startsWith(NODE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(NODE_COMMAND_PREFIX.length());
            var nodeFileDir = SceneFileUtils.getElementFileUnzipDir(systemType, sceneId, elementId);
            // 为兼容在节点目录内执行系统命令，执行相对路径的命令
            if (isWindows) {
                parsedCommands.add(CommandUtils.windowsCdDir(nodeFileDir));
                // 确保call执行不退出
                parsedCommands.add(String.format(WINDOWS_CALL_TEMPLATE, relativeCommand));
            } else {
                parsedCommands.add(CommandUtils.linuxCdDir(nodeFileDir));
                parsedCommands.add(relativeCommand);

                // 对于linux节点文件内命令，相对路径无法执行，再尝试执行一遍绝对路径
                var absoluteCommand = SystemTypeCommandUtils.joinPath(systemType, nodeFileDir, relativeCommand);
                parsedCommands.add(absoluteCommand);
            }
        } else {
            parsedCommands.add(command);
        }

        return parsedCommands;
    }

}
