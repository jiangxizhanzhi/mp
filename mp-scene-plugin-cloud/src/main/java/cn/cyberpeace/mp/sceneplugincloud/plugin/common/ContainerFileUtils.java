package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SceneFileUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ContainerFileUtils {

    public static final String CONTAINER_HELP_INSTALLER_GROUP = "container_installer";

    public static final String UNZIP_DST_PATH = "/usr/bin/";

    public static final String UNZIP_EXECUTOR_NAME = "unzip";

    public static final String APK_UNZIP_EXECUTOR_NAME = "unzip.apk";

    public static final String CENTOS6_UNZIP_EXECUTOR_NAME = "unzip_centos6.x86_64.rpm";

    public static final String CENTOS5_UNZIP_EXECUTOR_NAME = "unzip_centos5";

    public static final String CONTAINER_COPY_FILE_PREFIX = "container_copy_file:";

    public static final String VERSION_TEST_COMMAND = "if ! command -v unzip &> /dev/null;then if ! command -v apk &> /dev/null;" +
            "then if [ -e /etc/system-release ];then       cat /etc/system-release;" +
            "elif [ -e /etc/redhat-release ];then cat /etc/redhat-release;else " +
            "echo no_system_release;" +
            "fi else echo apk_exist;" +
            "fi else echo unzip_exist;" +
            "fi";

    public static String unzipLocalFilepath = null;

    public static String apkUnzipLocalFilepath = null;

    public static String centos5UnzipLocalFilepath = null;

    public static String centos6UnzipLocalFilepath = null;

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

    public static String getCentos6UnzipLocalFilepath() {
        if (!StringUtils.isEmpty(centos6UnzipLocalFilepath)) {
            return centos6UnzipLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, CENTOS6_UNZIP_EXECUTOR_NAME);
        centos6UnzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return centos6UnzipLocalFilepath;
    }

    public static String getCentos5UnzipLocalFilepath() {
        if (!StringUtils.isEmpty(centos5UnzipLocalFilepath)) {
            return centos5UnzipLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, CENTOS5_UNZIP_EXECUTOR_NAME);
        centos5UnzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return centos5UnzipLocalFilepath;
    }


    public static boolean prepareUnzip(Container containerClient, String containerId, List<String> extraCommands) {
        List<String> commands = new ArrayList<>();
        commands.add(VERSION_TEST_COMMAND);
        commands.addAll(extraCommands);
        var result = containerClient.executeCommand(containerId, StringUtils.join(commands, " && "));
        if (result.getExitCode() == null || result.getExitCode() != 0 || StringUtils.isEmpty(result.getOutput())) {
            return false;
        }
        String output = result.getOutput();
        if (output.contains("unzip_exist")) {
            return true;
        } else if (output.contains("apk_exist")) {
            
            return prepareApkUnzip(containerClient, containerId);
        } else if (output.contains("CentOS release 6")) {
            
            return prepareCentos6Unzip(containerClient, containerId);
        } else if (output.contains("release 5")) {
            return prepareCentos5Unzip(containerClient, containerId);
        } else {
            var unzipLocalFilepath = getUnzipLocalFilepath();
            if (StringUtils.isEmpty(unzipLocalFilepath)) {
                return false;
            }
            var flag = containerClient.copyFile(containerId, unzipLocalFilepath, UNZIP_DST_PATH);
            containerClient.executeCommand(containerId, String.format("chmod +x %s", UNZIP_DST_PATH + UNZIP_EXECUTOR_NAME));
            return flag;
        }
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

    public static boolean prepareCentos6Unzip(Container containerClient, String containerId) {
        var unzipLocalFilepath = getCentos6UnzipLocalFilepath();
        if (StringUtils.isEmpty(unzipLocalFilepath)) {
            return false;
        }
        var flag = containerClient.copyFile(containerId, unzipLocalFilepath, "/tmp");
        containerClient.executeCommand(containerId, String.format("rpm -ivh %s", "/tmp/" + CENTOS6_UNZIP_EXECUTOR_NAME));
        return flag;
    }

    public static boolean prepareCentos5Unzip(Container containerClient, String containerId) {
        var unzipLocalFilepath = getCentos5UnzipLocalFilepath();
        if (StringUtils.isEmpty(unzipLocalFilepath)) {
            return false;
        }
        var flag = containerClient.copyFile(containerId, unzipLocalFilepath, UNZIP_DST_PATH);
        containerClient.executeCommand(containerId, String.format("chmod +x %s", UNZIP_DST_PATH + CENTOS5_UNZIP_EXECUTOR_NAME));
        containerClient.executeCommand(containerId, String.format("mv %s /usr/bin/unzip", UNZIP_DST_PATH + CENTOS5_UNZIP_EXECUTOR_NAME));
        return flag;
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

    public static boolean prepareZipFile(Container containerClient, String containerId, String fileUrl,String standardSystemType, String unzipDir,boolean unzipDirExists) {
        var toPath = StringUtils.join(unzipDir, SceneFileUtils.ZIP_FILE_SUFFIX);
        if (!unzipDirExists) {
            containerClient.executeCommand(containerId, CommandUtils.linuxMkDir(unzipDir));
        }
        var filename = FileUtil.getName(toPath);
        var tmpZipFilepath = FsCommon.downloadTmp(URLUtil.getPath(fileUrl), filename);
        var toDir = toPath.substring(0, toPath.length() - filename.length());
        try {
            containerClient.copyFile(containerId, tmpZipFilepath, toDir);
        } finally {
            FileUtil.del(tmpZipFilepath);
        }
        String commands = SystemTypeCommandUtils.inlineJoinCommands(standardSystemType, List.of(CommandUtils.linuxUnzipFile(toPath, unzipDir), String.format("chmod +x %s/*", unzipDir)).toArray(new String[0]));
        containerClient.executeCommand(containerId, commands);
        return true;
    }

    public static boolean prepareLocalFile(Container containerClient, String containerId, String localPath, String destinationPath) {
        if (StringUtils.isEmpty(localPath)) {
            return false;
        }
        return containerClient.copyFile(containerId, localPath, destinationPath);
    }

}
