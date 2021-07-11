package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SceneFileUtils {

    public static final String SCENE_FILE_DIR_NAME = "scene_file";

    public static final String FILE_URL_SEPARATOR = "/";

    public static final String ZIP_FILE_SUFFIX = ".zip";

    
    public static String getSceneFileDir(String systemType, String sceneId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME);
        }
    }

    
    public static String getSceneFileUnzipDir(String systemType, String sceneId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, sceneId);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, sceneId);
        }
    }

    
    public static String getElementFileUnzipDir(String systemType, String sceneId, String elementId) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getWindowsInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, elementId);
        } else {
            return SystemTypeCommandUtils.joinPath(systemType, SceneSingleton.sceneAppConfig.getLinuxInstallerDir(), sceneId,
                    SCENE_FILE_DIR_NAME, elementId);
        }
    }

    
    public static List<String> genZipFileInitCommand(String systemType, String fileUrl, String toPath, String unzipDir) {
        var metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        var absoluteFileUrl = StringUtils.joinWith(FILE_URL_SEPARATOR, metadataFileServer, fileUrl);
        var filename = FileUtil.getName(toPath);
        var toPathDir = toPath.substring(0, toPath.length() - filename.length());
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
                    CommandUtils.linuxUnzipFile(toPath, unzipDir),
                    CommandUtils.linuxChmodX(SystemTypeCommandUtils.joinPath(systemType, unzipDir, "*"))
            );
        }
        return commands;
    }

    public static List<String> genSceneFileInitCommand(String systemType, String fileUrl, String sceneId) {
        var unzipDir = getSceneFileUnzipDir(systemType, sceneId);
        var toPath = StringUtils.join(unzipDir, ZIP_FILE_SUFFIX);
        return genZipFileInitCommand(systemType, fileUrl, toPath, unzipDir);
    }

    public static List<String> genElementFileInitCommand(String systemType, String fileUrl, String sceneId, String elementId) {
        var unzipDir = getElementFileUnzipDir(systemType, sceneId, elementId);
        var toPath = StringUtils.join(unzipDir, ZIP_FILE_SUFFIX);
        return genZipFileInitCommand(systemType, fileUrl, toPath, unzipDir);
    }

}
