package cn.cyberpeace.mp.scene.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SceneCommandUtils {

    public static final String ORIGIN_COMMAND_PREFIX = "o:";

    public static final String SCENE_COMMAND_PREFIX = "s:";

    public static final String NODE_COMMAND_PREFIX = "n:";

    public static final String WINDOWS_CALL_TEMPLATE = "call %s";

    
    public static List<String> parseCommand(String command, String sceneId, String elementId, String systemType) {
        List<String> parsedCommands = new ArrayList<>();
        boolean isWindows = SystemTypeCommandUtils.isWindows(systemType);
        if (command == null) {
            return new ArrayList<>();
        }
        if (command.startsWith(ORIGIN_COMMAND_PREFIX)) {
            String commandContent = command.substring(ORIGIN_COMMAND_PREFIX.length());
            if (StringUtils.isNotEmpty(commandContent)) {
                parsedCommands.add(commandContent);
            }
        } else if (command.startsWith(SCENE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(SCENE_COMMAND_PREFIX.length());
            if (StringUtils.isNotEmpty(relativeCommand)) {
                var sceneFileDir = SceneFileUtils.getSceneFileUnzipDir(systemType, sceneId);
                
                if (isWindows) {
                    parsedCommands.add(CommandUtils.windowsCdDir(sceneFileDir));
                    
                    parsedCommands.add(String.format(WINDOWS_CALL_TEMPLATE, relativeCommand));
                } else {
                    
                    var absoluteCommand = SystemTypeCommandUtils.joinPath(systemType, sceneFileDir, relativeCommand);
                    parsedCommands.add(absoluteCommand);

                    parsedCommands.add(CommandUtils.linuxCdDir(sceneFileDir));
                    parsedCommands.add(relativeCommand);
                }
            }
        } else if (command.startsWith(NODE_COMMAND_PREFIX)) {
            var relativeCommand = command.substring(NODE_COMMAND_PREFIX.length());
            if (StringUtils.isNotEmpty(relativeCommand)) {
                var nodeFileDir = SceneFileUtils.getElementFileUnzipDir(systemType, sceneId, elementId);
                
                if (isWindows) {
                    parsedCommands.add(CommandUtils.windowsCdDir(nodeFileDir));
                    
                    parsedCommands.add(String.format(WINDOWS_CALL_TEMPLATE, relativeCommand));

                    parsedCommands.addAll(parseDirCommands(command, CommandUtils.WINDOWS_PATH_DELIMITER));
                } else {
                    
                    var absoluteCommand = SystemTypeCommandUtils.joinPath(systemType, nodeFileDir, relativeCommand);
                    parsedCommands.add(absoluteCommand);

                    parsedCommands.add(CommandUtils.linuxCdDir(nodeFileDir));
                    parsedCommands.add(relativeCommand);

                    parsedCommands.addAll(parseDirCommands(relativeCommand, CommandUtils.LINUX_PATH_DELIMITER));
                }
            }
        } else {
            parsedCommands.add(command);
        }

        return parsedCommands;
    }

    private static List<String> parseDirCommands(String command, String pathDelimiter) {
        if (!command.contains(pathDelimiter)) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        String[] commands = StringUtils.split(command, pathDelimiter);
        for (int i = 0; i < commands.length; i++) {
            if (i == commands.length - 1) {
                result.add("./" + commands[i]);
            } else {
                result.add(CommandUtils.linuxCdDir(commands[i]));
            }
        }
        return result;
    }
}
