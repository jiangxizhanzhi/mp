package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SceneFileUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ContainerFileUtils {

    public static final String CONTAINER_HELP_INSTALLER_GROUP = "container_installer";

    public static final String UNZIP_DST_PATH = "/usr/bin/";

    public static final String UNZIP_EXECUTOR_NAME = "unzip";

    public static final String UNZIP_DOWNLOAD_SCRIPT_FILE_NAME = "unzip_download.sh";

    public static final String UNZIP_DOWNLOAD_SCRIPT_FILE_PATH = "/tmp";

    public static final String APK_UNZIP_EXECUTOR_NAME = "unzip.apk";

    public static final String CONTAINER_COPY_FILE_PREFIX = "container_copy_file:";

    public static String unzipLocalFilepath = null;

    public static String apkUnzipLocalFilepath = null;

    public static String unzipDownloadScriptFilePath = null;

    public static Map<String, String> unzipRemoteFilepath = null;

    public static Map<String, String> getUnzipRemoteFilepath() {
        if (unzipRemoteFilepath != null) {
            return unzipRemoteFilepath;
        }
        var list = LocalFileMappingCommon.getMappings(CONTAINER_HELP_INSTALLER_GROUP);
        unzipRemoteFilepath = new HashMap<>();
        list.forEach(f -> {
            if (f.getName().contains(UNZIP_EXECUTOR_NAME)) {
                unzipRemoteFilepath.put(f.getName(), f.getRemoteFile());
            }
        });
        return unzipRemoteFilepath;
    }

    public static String getUnzipDownloadScriptLocalFilepath() {
        if (!StringUtils.isEmpty(unzipDownloadScriptFilePath)) {
            return unzipDownloadScriptFilePath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, UNZIP_DOWNLOAD_SCRIPT_FILE_NAME);
        unzipDownloadScriptFilePath = mapping != null ? mapping.getLocalFile() : null;
        return unzipDownloadScriptFilePath;
    }

    public static String getUnzipLocalFilepath() {
        if (!StringUtils.isEmpty(unzipLocalFilepath)) {
            return unzipLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, UNZIP_EXECUTOR_NAME);
        unzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return unzipLocalFilepath;
    }

    public static String getApkUnzipLocalFilepath() {
        if (!StringUtils.isEmpty(apkUnzipLocalFilepath)) {
            return apkUnzipLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, APK_UNZIP_EXECUTOR_NAME);
        apkUnzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return apkUnzipLocalFilepath;
    }

    public static boolean prepareUnzipV2(Container containerClient, String containerId) {
        var unzipFileName2Paths = getUnzipRemoteFilepath();
        var unzipDownloadScriptFilePath = getUnzipDownloadScriptLocalFilepath();
        if (StringUtils.isEmpty(unzipDownloadScriptFilePath)) {
            return false;
        }
        if (unzipFileName2Paths.isEmpty()) {
            return false;
        }
        var flag = containerClient.copyFile(containerId, unzipDownloadScriptFilePath, UNZIP_DOWNLOAD_SCRIPT_FILE_PATH);
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        List<String> params =
                unzipFileName2Paths.entrySet().stream()
                        .map(e -> {
                            String finalUrl = String.format("%s/%s", metadataFileServer, e.getValue());
                            return StringUtils.joinWith("=", e.getKey(), finalUrl);
                        })
                        .collect(Collectors.toList());
        String cmd = String.format("sh %s %s", UNZIP_DOWNLOAD_SCRIPT_FILE_PATH + "/" + UNZIP_DOWNLOAD_SCRIPT_FILE_NAME, StringUtils.join(params, " "));
        containerClient.executeCommand(containerId, cmd);
        return flag;
    }


    public static boolean prepareUnzip(Container containerClient, String containerId) {
        var unzipLocalFilepath = getUnzipLocalFilepath();
        if (StringUtils.isEmpty(unzipLocalFilepath)) {
            return false;
        }
        var flag = containerClient.copyFile(containerId, unzipLocalFilepath, UNZIP_DST_PATH);
        containerClient.executeCommand(containerId, String.format("chmod +x %s", UNZIP_DST_PATH + UNZIP_EXECUTOR_NAME));
        return flag;
    }

    public static boolean prepareApkUnzip(Container containerClient, String containerId) {
        var result = containerClient.executeCommand(containerId, "sh -c \"command -v apk\"");
        if (result.getExitCode() != null && result.getExitCode() == 0 && StringUtils.isNotEmpty(result.getOutput()) && result.getOutput().contains("/apk")) {
            var apkUnzipLocalFilepath = getApkUnzipLocalFilepath();
            if (StringUtils.isEmpty(apkUnzipLocalFilepath)) {
                return false;
            }
            var flag = containerClient.copyFile(containerId, apkUnzipLocalFilepath, UNZIP_DST_PATH);
            containerClient.executeCommand(containerId, String.format("apk add --no-network %s", UNZIP_DST_PATH + APK_UNZIP_EXECUTOR_NAME));
            return flag;
        }
        return false;
    }

    public static boolean prepareZipFile(Container containerClient, String containerId, String fileUrl, String unzipDir) {
        var toPath = StringUtils.join(unzipDir, SceneFileUtils.ZIP_FILE_SUFFIX);
        containerClient.executeCommand(containerId, CommandUtils.linuxMkDir(unzipDir));
        var filename = FileUtil.getName(toPath);
        var tmpZipFilepath = FsCommon.downloadTmp(URLUtil.getPath(fileUrl), filename);
        var toDir = toPath.substring(0, toPath.length() - filename.length());;
        try {
            containerClient.copyFile(containerId, tmpZipFilepath, toDir);
        } finally {
            FileUtil.del(tmpZipFilepath);
        }
        containerClient.executeCommand(containerId, CommandUtils.linuxUnzipFile(toPath, unzipDir));
        containerClient.executeCommand(containerId, String.format("chmod +x %s/*", unzipDir));
        return true;
    }

    public static boolean prepareLocalFile(Container containerClient, String containerId, String localPath, String destinationPath) {
        if (StringUtils.isEmpty(localPath)) {
            return false;
        }
        return containerClient.copyFile(containerId, localPath, destinationPath);
    }

}
