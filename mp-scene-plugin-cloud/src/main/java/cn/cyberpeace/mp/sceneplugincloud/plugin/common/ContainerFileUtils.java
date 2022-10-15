package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.SceneFileUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ContainerFileUtils {

    public static final String CONTAINER_HELP_INSTALLER_GROUP = "container_installer";

    public static final String UNZIP_DOWNLOAD_SCRIPT_FILE_NAME = "unzip_version.sh";

    public static final String CONTAINER_COPY_FILE_PREFIX = "container_copy_file:";

    public static final String UNZIP_DOWNLOAD_SCRIPT_FILE_PATH = "/tmp";

    public static String unzipVersionScriptFilePath = null;

    public static String getUnzipVersionScriptLocalFilepath() {
        if (!StringUtils.isEmpty(unzipVersionScriptFilePath)) {
            return unzipVersionScriptFilePath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, UNZIP_DOWNLOAD_SCRIPT_FILE_NAME);
        unzipVersionScriptFilePath = mapping != null ? mapping.getLocalFile() : null;
        return unzipVersionScriptFilePath;
    }

    public static boolean prepareUnzip(Container containerClient, String containerId) {
        var unzipDownloadScriptFilePath = getUnzipVersionScriptLocalFilepath();
        if (StringUtils.isEmpty(unzipDownloadScriptFilePath)) {
            return false;
        }
        containerClient.copyFile(containerId, unzipDownloadScriptFilePath, UNZIP_DOWNLOAD_SCRIPT_FILE_PATH);
        String cmd = String.format("sh %s", UNZIP_DOWNLOAD_SCRIPT_FILE_PATH + "/" + UNZIP_DOWNLOAD_SCRIPT_FILE_NAME);
        var cmdResult = containerClient.executeCommand(containerId, cmd);
        UnzipFileInstaller unzipFileInstaller = new DefaultUnzipFileInstaller();
        if (StringUtils.isNotEmpty(cmdResult.getOutput())) {
            String unzipExecutorName = cmdResult.getOutput();
            if (ApkUnzipFileInstaller.UNZIP_EXECUTOR_NAME.equals(unzipExecutorName)) {
                unzipFileInstaller = new ApkUnzipFileInstaller();
            } else if (Centos6UnzipFileInstaller.UNZIP_EXECUTOR_NAME.equals(unzipExecutorName)) {
                unzipFileInstaller = new Centos6UnzipFileInstaller();
            }
        }
        return unzipFileInstaller.prepareUnzip(containerClient, containerId);
    }

    public static boolean prepareZipFile(Container containerClient, String containerId, String fileUrl, String unzipDir) {
        var toPath = StringUtils.join(unzipDir, SceneFileUtils.ZIP_FILE_SUFFIX);
        containerClient.executeCommand(containerId, CommandUtils.linuxMkDir(unzipDir));
        var filename = FileUtil.getName(toPath);
        var tmpZipFilepath = FsCommon.downloadTmp(URLUtil.getPath(fileUrl), filename);
        var toDir = toPath.substring(0, toPath.length() - filename.length());
        ;
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


    private static abstract class UnzipFileInstaller {
        private String unzipLocalFilepath;

        private String unzipExecutorName;

        private String unzipDstPath;

        public UnzipFileInstaller(String unzipDstPath, String unzipExecutorName) {
            this.unzipDstPath = unzipDstPath;
            this.unzipExecutorName = unzipExecutorName;
        }

        public abstract boolean prepareUnzip(Container containerClient, String containerId);

        protected String getUnzipDstPath() {
            return unzipDstPath;
        }

        protected String getUnzipLocalFilepath() {
            if (!StringUtils.isEmpty(unzipLocalFilepath)) {
                return unzipLocalFilepath;
            }

            LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(CONTAINER_HELP_INSTALLER_GROUP, unzipExecutorName);
            unzipLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
            return unzipLocalFilepath;
        }
    }

    private static class DefaultUnzipFileInstaller extends UnzipFileInstaller {
        public static final String UNZIP_DST_PATH = "/usr/bin/";

        public static final String UNZIP_EXECUTOR_NAME = "unzip";

        public DefaultUnzipFileInstaller() {
            super(UNZIP_DST_PATH, UNZIP_EXECUTOR_NAME);
        }

        @Override
        public boolean prepareUnzip(Container containerClient, String containerId) {
            var unzipLocalFilepath = getUnzipLocalFilepath();
            if (StringUtils.isEmpty(unzipLocalFilepath)) {
                return false;
            }
            var flag = containerClient.copyFile(containerId, unzipLocalFilepath, getUnzipDstPath());
            containerClient.executeCommand(containerId, String.format("chmod +x %s", getUnzipDstPath() + UNZIP_EXECUTOR_NAME));
            return flag;
        }
    }

    private static class ApkUnzipFileInstaller extends UnzipFileInstaller {
        public static final String UNZIP_DST_PATH = "/usr/bin/";

        public static final String UNZIP_EXECUTOR_NAME = "unzip.apk";

        public ApkUnzipFileInstaller() {
            super(UNZIP_DST_PATH, UNZIP_EXECUTOR_NAME);
        }

        @Override
        public boolean prepareUnzip(Container containerClient, String containerId) {
            var apkUnzipLocalFilepath = getUnzipLocalFilepath();
            if (StringUtils.isEmpty(apkUnzipLocalFilepath)) {
                return false;
            }
            var flag = containerClient.copyFile(containerId, apkUnzipLocalFilepath, getUnzipDstPath());
            containerClient.executeCommand(containerId, String.format("apk add --no-network %s", getUnzipDstPath() + UNZIP_EXECUTOR_NAME));
            return flag;
        }
    }

    private static class Centos6UnzipFileInstaller extends UnzipFileInstaller {
        public static final String UNZIP_DST_PATH = "/tmp/";

        public static final String UNZIP_EXECUTOR_NAME = "unzip_centos6.x86_64.rpm";

        public Centos6UnzipFileInstaller() {
            super(UNZIP_DST_PATH, UNZIP_EXECUTOR_NAME);
        }

        @Override
        public boolean prepareUnzip(Container containerClient, String containerId) {
            var apkUnzipLocalFilepath = getUnzipLocalFilepath();
            if (StringUtils.isEmpty(apkUnzipLocalFilepath)) {
                return false;
            }
            var flag = containerClient.copyFile(containerId, apkUnzipLocalFilepath, getUnzipDstPath());
            containerClient.executeCommand(containerId, String.format("rpm -ivh %s", getUnzipDstPath() + UNZIP_EXECUTOR_NAME));
            return flag;
        }
    }
}
