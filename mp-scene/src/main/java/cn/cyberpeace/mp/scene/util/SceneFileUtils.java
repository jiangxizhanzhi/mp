package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SceneFileUtils {

    public static final String SCENE_FILE_DIR_NAME = "scene_file";

    public static final String FILE_URL_SEPARATOR = "/";

    public static final String ZIP_FILE_SUFFIX = ".zip";

    /**
     * 生成场景文件目录
     * @param systemType 网元系统类型
     * @param sceneId 场景id
     * @return 场景文件目录
     */
    public static String getSceneFileDir(String systemType, String sceneId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME);
        }
    }

    /**
     * 生成场景文件解压目录
     * @param systemType 网元系统类型
     * @param sceneId 场景id
     * @return 场景文件目录
     */
    public static String getSceneFileUnzipDir(String systemType, String sceneId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, sceneId);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, sceneId);
        }
    }

    /**
     * 生成节点文件解压目录
     * @param systemType 网元系统类型
     * @param sceneId 场景id
     * @param elementId 节点id
     * @return 场景文件目录
     */
    public static String getElementFileUnzipDir(String systemType, String sceneId, String elementId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, elementId);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, elementId);
        }
    }

    /**
     * 生成场景zip文件初始化命令
     * @param systemType 网元系统类型
     * @param fileUrl 文件url
     * @param toPath 目的路径
     * @return 初始化命令
     */
    public static String genZipFileInitCommand(String systemType, String fileUrl, String toPath, String unzipDir) {
        var metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        var absoluteFileUrl = StringUtils.joinWith(FILE_URL_SEPARATOR, metadataFileServer, fileUrl);
        var toPathDir = FileUtil.getParent(toPath, 1);
        List<String> commands;
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            commands = List.of(
                    CommandUtils.windowsMkDir(toPathDir),
                    CommandUtils.windowsMkDir(unzipDir),
                    CommandUtils.windowsDownloadFile(absoluteFileUrl, toPath),
                    CommandUtils.windowsUnzipFile(toPath, unzipDir)
            );
        } else {
            commands = List.of(
                    CommandUtils.linuxMkDir(toPathDir),
                    CommandUtils.linuxMkDir(unzipDir),
                    CommandUtils.linuxDownloadFile(absoluteFileUrl, toPath),
                    CommandUtils.linuxUnzipFile(toPath, unzipDir)
            );
        }
        return SystemTypeCommandUtils.joinCommands(systemType, commands.toArray(new String[0]));
    }

    public static String genSceneFileInitCommand(String systemType, String fileUrl, String sceneId) {
        var unzipDir = getSceneFileUnzipDir(systemType, sceneId);
        var toPath = StringUtils.join(unzipDir, ZIP_FILE_SUFFIX);
        return genZipFileInitCommand(systemType, fileUrl, toPath, unzipDir);
    }

    public static String genElementFileInitCommand(String systemType, String fileUrl, String sceneId, String elementId) {
        var unzipDir = getElementFileUnzipDir(systemType, sceneId, elementId);
        var toPath = StringUtils.join(unzipDir, ZIP_FILE_SUFFIX);
        return genZipFileInitCommand(systemType, fileUrl, toPath, unzipDir);
    }

}
