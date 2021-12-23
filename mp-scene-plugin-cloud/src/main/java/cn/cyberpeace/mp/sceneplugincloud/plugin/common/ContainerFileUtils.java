package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SceneFileUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

public class ContainerFileUtils {

    public static final String CONTAINER_HELP_INSTALLER_GROUP = "container_installer";

    public static final String UNZIP_DST_PATH = "/usr/bin/";

    public static final String UNZIP_EXECUTOR_NAME = "unzip";

    public static String unzipLocalFilepath = null;

    public static String getUnzipLocalFilepath() {
        if (!StringUtils.isEmpty(unzipLocalFilepath)) {
            return unzipLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, UNZIP_EXECUTOR_NAME);
        unzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return unzipLocalFilepath;
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

    public static boolean prepareZipFile(Container containerClient, String containerId, String fileUrl, String unzipDir) {
        var toPath = StringUtils.join(unzipDir, SceneFileUtils.ZIP_FILE_SUFFIX);
        containerClient.executeCommand(containerId, CommandUtils.linuxMkDir(unzipDir));
        var filename = FileUtil.getName(toPath);
        var tmpZipFilepath = FdfsCommon.downloadTmp(fileUrl, filename);
        var toDir = toPath.substring(0, toPath.length() - filename.length());;
        try {
            containerClient.copyFile(containerId, tmpZipFilepath, toDir);
        } finally {
//            FileUtil.del(tmpZipFilepath);
        }
        containerClient.executeCommand(containerId, CommandUtils.linuxUnzipFile(toPath, unzipDir));
        return true;
    }

}
