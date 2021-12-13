package cn.cyberpeace.mp.scene.util;

public class SceneCommandUtils {

    public static final String ORIGIN_COMMAND_PREFIX = "o:";

    public static final String SCENE_COMMAND_PREFIX = "s:";

    public static final String NODE_COMMAND_PREFIX = "n:";

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
        if (command.startsWith(ORIGIN_COMMAND_PREFIX)) {
            return command.substring(ORIGIN_COMMAND_PREFIX.length());
        } else if (command.startsWith(SCENE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(SCENE_COMMAND_PREFIX.length());
            return SystemTypeCommandUtils.joinPath(systemType, SceneFileUtils.getSceneFileUnzipDir(systemType, sceneId),
                    relativeCommand);
        } else if (command.startsWith(NODE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(NODE_COMMAND_PREFIX.length());
            return SystemTypeCommandUtils.joinPath(systemType, SceneFileUtils.getElementFileUnzipDir(systemType, sceneId, elementId),
                    relativeCommand);
        } else {
            return command;
        }
    }

}
