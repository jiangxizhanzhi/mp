package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.hutool.core.io.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


// TODO: 调用CommandUtils优化操作命令
@Slf4j
@Component
public class InstallerUtils {

    /**
     * windows创建基础目录
     */
    public static final String[] WINDOWS_BASE_DIR_CREATE = {
            "mkdir %s",
            "mkdir %s\\custom_install",
    };

    /**
     * windows自定义安装
     */
    public static final String[] WINDOWS_CUSTOM_NORMAL_INSTALL = {
            "cd %s",
            "%s",
    };

    /**
     * windows自定义解压安装
     */
    public static final String[] WINDOWS_CUSTOM_UNZIP_INSTALL = {
            "mkdir %s\\custom_install\\%s",
            "unzip -o \"%s\" -d \"%s\\custom_install\\%s\"",
            "call \"%s\\custom_install\\%s\\%s\"",
    };

    /**
     * windows安装脚本安装
     */
    public static final String[] WINDOWS_MAKE_SCRIPT_INSTALL = {
            "curl -o \"%s\\custom_install\\install.bat\" %s",
            "call \"%s\\custom_install\\install.bat\"",
    };

    /**
     * windows文件下载
     */
    public static final String WINDOWS_FILE_DOWNLOAD = "curl -o \"%s\" %s";

    /**
     * windows默认安装
     */
    public static final String WINDOWS_DEFAULT_INSTALL_SOFTWARE = "start /b %s";

    /**
     * windows特殊默认安装
     */
    public static final String WINDOWS_SPECIAL_DEFAULT_INSTALL_SOFTWARE = "echo %s>>%s\\file.txt";

    /**
     * windows特殊默认安装 启动
     */
    public static final String WINDOWS_SPECIAL_DEFAULT_INSTALL_SOFTWARE_START = "start /b %s\\start.exe";

    /**
     * linux创建基础目录
     */
    public static final String[] LINUX_BASE_DIR_CREATE = {
            "mkdir %s",
            "mkdir %s/custom_install",
    };

    /**
     * linux自定义安装
     */
    public static final String[] LINUX_CUSTOM_NORMAL_INSTALL = {
            "cd %s",
            "%s",
    };

    /**
     * linux自定义解压安装
     */
    public static final String[] LINUX_CUSTOM_UNZIP_INSTALL = {
            "mkdir %s/custom_install/%s",
            "unzip -o %s -d %s/custom_install/%s",
            "cd %s/custom_install/%s",
            "chmod +x %s/custom_install/%s/*.sh",
            "/bin/bash %s/custom_install/%s/%s",
            "sync",
    };

    public static final String[] LINUX_DOCKER_TAR_INSTALL = {
            "mkdir -p %s/docker_install/%s",
            "cd %s/docker_install/%s",
            "repo_tag=$(docker load -i %s | grep 'Loaded image' | awk '{print $3}')",
            "%s"
    };

    public static final String[] LINUX_DOCKER_ZIP_INSTALL = {
            "mkdir -p %s/docker_install/%s",
            "unzip -o %s -d %s/docker_install/%s",
            "cd %s/docker_install/%s",
            "repo_tag=$(docker load -i %s | grep 'Loaded image' | awk '{print $3}')",
            "%s"
    };

    /**
     * linux文件下载
     */
    public static final String LINUX_FILE_DOWNLOAD = "curl -o \"%s\" %s";

    /**
     * 容器文件复制
     */
    public static final String CONTAINER_FILE_COPY = "copy_file %s %s";

    /**
     * linux rpm默认安装
     */
    public static final String LINUX_RPM_DEFAULT_INSTALL_SOFTWARE = "rpm -ivh %s";

    /**
     * linux deb默认安装
     */
    public static final String LINUX_DEB_DEFAULT_INSTALL_SOFTWARE = "apt-get install %s";

    public static final String INSTALLER_REPLACEMENT = "{this}";

    public static final String WINDOWS_HELP_INSTALLER_GROUP = "windows_help_installer";

    public static final String FILE_URL_SEPARATOR = "/";

    @SuppressWarnings({"java:S1144", "java:S3010"})
    private InstallerUtils() {}

    public static boolean isNewerWindows(String systemType) {
        systemType = systemType.toLowerCase();
        return BaseSceneElementModel.SystemType.WINDOWS.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_10.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_8.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_7.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_VISTA.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2019.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2016.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2012.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2010.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2008.equals(systemType);
    }

    public static boolean isOlderWindows(String systemType) {
        systemType = systemType.toLowerCase();
        return BaseSceneElementModel.SystemType.WINDOWS_XP.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2003.equals(systemType)
                || BaseSceneElementModel.SystemType.WINDOWS_SERVER_2000.equals(systemType);
    }

    public static String getDefaultInstallDir(String systemType) {
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return SceneSingleton.sceneAppConfig.getWindowsInstallerDir();
        } else {
            return SceneSingleton.sceneAppConfig.getLinuxInstallerDir();
        }
    }

    public static String generateFilename(String name, String suffix) {
        return StringUtils.joinWith("\\.", name, suffix);
    }

    /**
     * 生成基础目录创建命令
     * @param systemType 系统类型
     * @return 基础目录创建命令
     */
    public static String generateBaseDirCreateCommand(String systemType) {
        var installerDir = getDefaultInstallDir(systemType);
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            return String.format(SystemTypeCommandUtils.joinCommands(systemType, WINDOWS_BASE_DIR_CREATE), installerDir,
                    installerDir);
        } else {
            return String.format(SystemTypeCommandUtils.joinCommands(systemType, LINUX_BASE_DIR_CREATE), installerDir,
                    installerDir);
        }
    }

    /**
     * 生成文件下载命令
     * @param systemType 系统类型
     * @param fileUrl 文件url
     * @param fileName 文件名称
     * @return 文件下载命令
     */
    public static FileDownloadInfo generateFileDownloadCommand(String systemType, String fileUrl, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            fileName = FileUtil.getName(fileUrl);
        }
        var installerDir = getDefaultInstallDir(systemType);
        String filePath;
        String downloadCommand;
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            filePath = String.format("%s\\%s", installerDir, fileName);
            downloadCommand = String.format(WINDOWS_FILE_DOWNLOAD, filePath, fileUrl);
        } else {
            filePath = String.format("%s/%s", installerDir, fileName);
            downloadCommand = String.format(LINUX_FILE_DOWNLOAD, filePath, fileUrl);
        }

        return new FileDownloadInfo(filePath, downloadCommand);
    }

    /**
     * 生成文件复制命令
     * @param systemType 系统类型
     * @param fileUrl 文件url
     * @return 文件复制命令
     */
    public static FileDownloadInfo generateFileCopyCommand(String systemType, String fileUrl) {
        var installerDir = getDefaultInstallDir(systemType);
        String downloadCommand = String.format(CONTAINER_FILE_COPY, fileUrl, installerDir);
        return new FileDownloadInfo(fileUrl, downloadCommand);
    }

    /**
     * 生成默认的安装脚本
     * @param systemType 系统详细类型
     * @param filePath 文件路径
     * @return 默认的安装脚本
     */
    public static String generateDefaultInstallCommand(String systemType, String filePath) {
        var installerDir = getDefaultInstallDir(systemType);
        systemType = systemType.toLowerCase();
        if (isNewerWindows(systemType)) {
            return String.format(WINDOWS_SPECIAL_DEFAULT_INSTALL_SOFTWARE, filePath, installerDir);
        } else if (isOlderWindows(systemType)) {
            return String.format(WINDOWS_DEFAULT_INSTALL_SOFTWARE, filePath);
        } else if (BaseSceneElementModel.SystemType.CENTOS.equals(systemType)) {
            return String.format(LINUX_RPM_DEFAULT_INSTALL_SOFTWARE, filePath);
        } else if (BaseSceneElementModel.SystemType.UBUNTU.equals(systemType)
                || BaseSceneElementModel.SystemType.KALI.equals(systemType)) {
            return String.format(LINUX_DEB_DEFAULT_INSTALL_SOFTWARE, filePath);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 生成默认自定义安装命令
     * @param systemType 系统类型
     * @param filePath 文件路径
     * @param installCommand 安装命令
     * @return 默认自定义安装命令
     */
    public static String generateDefaultCustomInstallCommand(String systemType, String filePath,
                                                             String installCommand) {
        String filePathLower = filePath.toLowerCase();
        var installerDir = getDefaultInstallDir(systemType);
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            if (installCommand.contains(INSTALLER_REPLACEMENT)) {
                var fileName = FileUtil.getName(filePath);
                installCommand = installCommand.replace(INSTALLER_REPLACEMENT, fileName);
                return String.format(SystemTypeCommandUtils.joinCommands(systemType, WINDOWS_CUSTOM_NORMAL_INSTALL), installerDir,
                        installCommand);

            } else if (filePathLower.endsWith(".zip") || filePathLower.endsWith(".rar")) {
                var installFolder = TextUtils.uuid();
                return String.format(SystemTypeCommandUtils.joinCommands(systemType, WINDOWS_CUSTOM_UNZIP_INSTALL), installerDir,
                        installFolder, filePath, installerDir, installFolder, installerDir, installFolder,
                        installCommand);
            }
        } else {
            if (installCommand.contains(INSTALLER_REPLACEMENT)) {
                var fileName = FileUtil.getName(filePath);
                installCommand = installCommand.replace(INSTALLER_REPLACEMENT, fileName);
                return String.format(SystemTypeCommandUtils.joinCommands(systemType, LINUX_CUSTOM_NORMAL_INSTALL), installerDir,
                        installCommand);
            } else if (filePathLower.endsWith(".zip")) {
                var installFolder = TextUtils.uuid();
                return String.format(SystemTypeCommandUtils.joinCommands(systemType, LINUX_CUSTOM_UNZIP_INSTALL), installerDir,
                        installFolder, filePath, installerDir, installFolder, installerDir, installFolder, installerDir,
                        installFolder, installerDir, installFolder, installCommand);
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * 生成容器自定义安装命令
     * @param systemType 系统类型
     * @param filePath 复制文件源路径
     * @param installCommand 安装命令
     * @return 容器自定义安装命令
     */
    public static String generateContainerCustomInstallCommand(String systemType, String filePath, String installCommand) {
        String filePathLower = filePath.toLowerCase();
        var installerDir = getDefaultInstallDir(systemType);
        if (SystemTypeCommandUtils.isLinux(systemType)) {
            if (installCommand.contains(INSTALLER_REPLACEMENT)) {
                var fileName = FileUtil.getName(filePath);
                installCommand = installCommand.replace(INSTALLER_REPLACEMENT, fileName);
                return String.format(SystemTypeCommandUtils.joinCommands(systemType, LINUX_CUSTOM_NORMAL_INSTALL), installerDir,
                        installCommand);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 生成docker服务安装命令
     * @param systemType 系统类型
     * @param filePath 文件路径
     * @param installCommand 安装命令
     * @return 默认自定义安装命令
     */
    public static String generateDockerCommand(String systemType, String filePath, String imageName, String installCommand) {
        String filePathLower = filePath.toLowerCase();
        var installerDir = getDefaultInstallDir(systemType);
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            // todo 暂不支持 windows 的docker 服务
        } else {
            if (filePathLower.endsWith(".zip")) {
                var installFolder = TextUtils.uuid();
                return String.format(
                        SystemTypeCommandUtils.joinCommands(systemType, LINUX_DOCKER_ZIP_INSTALL),
                        installerDir, installFolder,
                        filePath, installerDir, installFolder,
                        installerDir, installFolder,
                        imageName,
                        installCommand);
            } else {
                var installFolder = TextUtils.uuid();
                return String.format(
                        SystemTypeCommandUtils.joinCommands(systemType, LINUX_DOCKER_TAR_INSTALL),
                        installerDir, installFolder,
                        installerDir, installFolder,
                        filePath,
                        installCommand);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取windows安装工具
     * @return windows安装工具
     */
    private static List<Installer> getWindowsInstallerTools() {
        var mappings = LocalFileMappingCommon.getMappings(WINDOWS_HELP_INSTALLER_GROUP);
        return mappings.stream().map(mapping -> {
            var installer = new Installer();
            installer.setName(mapping.getName());
            installer.setFile(mapping.getRemoteFile());
            return installer;
        }).collect(Collectors.toList());
    }

    /**
     * 生成安装命令
     * @param systemType 系统类型
     * @return 安装命令
     */
    public static List<String> generateInstallerInstallCommand(String systemType, List<Installer> installers) {
        List<String> installCommands = new ArrayList<>();
        installCommands.add(generateBaseDirCreateCommand(systemType));

        var hasDefaultInstaller = false;
        for (var installer : installers) {
            if (!StringUtils.isEmpty(installer.getFile()) && StringUtils.isEmpty(installer.getInstallCommand())) {
                hasDefaultInstaller = true;
                break;
            }
        }

        var metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        List<String> subInstallCommands = new ArrayList<>();
        if (hasDefaultInstaller && isNewerWindows(systemType)) {
            var windowsInstallerTools = getWindowsInstallerTools();
            for (var installerTool : windowsInstallerTools) {
                var installerToolUrl = StringUtils.joinWith(FILE_URL_SEPARATOR, metadataFileServer, installerTool.getFile());
                var fileDownloadInfo = generateFileDownloadCommand(systemType,
                        installerToolUrl, installerTool.getName());
                subInstallCommands.add(fileDownloadInfo.getDownloadCommand());
            }
        }

        boolean isWindowsOS = SystemTypeCommandUtils.isWindows(systemType);
        for (var installer : installers) {
            String installerFile = installer.getFile();
            String installCommand = installer.getInstallCommand();
            if (StringUtils.isEmpty(installerFile) && StringUtils.isEmpty(installer.getCopyFile())) {
                if (StringUtils.isEmpty(installCommand)) {
                    continue;
                }

                boolean isWinCommandContainZh = isWindowsOS && TextUtils.containChinese(installCommand);
                if (isWinCommandContainZh) {
                    installCommand = CommandUtils.fixWinZhCommand(installCommand);
                }

                subInstallCommands.add(installCommand);
            } else {
                var fileName = FileUtil.getName(installerFile);
                var fileUrl = StringUtils.joinWith(FILE_URL_SEPARATOR, metadataFileServer, installerFile);
                boolean isWinFileContainZh = isWindowsOS && TextUtils.containChinese(fileName);
                String downloadFileName;
                if (isWinFileContainZh) {
                    downloadFileName = generateFilename(TextUtils.uuid(), FileUtil.getSuffix(fileName));
                } else {
                    downloadFileName = fileName;
                }
                var fileDownloadInfo = new FileDownloadInfo();
                if (StringUtils.isNotEmpty(installer.getCopyFile())) {
                    fileDownloadInfo = generateFileCopyCommand(systemType, installer.getCopyFile());
                } else {
                    fileDownloadInfo = generateFileDownloadCommand(systemType, fileUrl, downloadFileName);
                }
                subInstallCommands.add(fileDownloadInfo.getDownloadCommand());

                var filePath = fileDownloadInfo.getFilePath();
                List<String> innerCommands = new ArrayList<>();
                if (isWinFileContainZh) {
                    innerCommands.add(String.format("ren %s %s", filePath, fileName));
                    filePath = filePath.replace(downloadFileName, fileName);
                }

                if (installer.isDocker()) {
                    innerCommands.add(generateDockerCommand(systemType, filePath, installer.getName(), installCommand));
                } else if (StringUtils.isEmpty(installCommand)) {
                    innerCommands.add(generateDefaultInstallCommand(systemType, filePath));
                } else {
                    if (StringUtils.isNotEmpty(installer.getCopyFile())) {
                        installCommand = generateContainerCustomInstallCommand(systemType, filePath, installCommand);
                    } else {
                        installCommand = generateDefaultCustomInstallCommand(systemType, filePath, installCommand);
                    }
                    boolean isWinCommandContainZh = isWindowsOS && TextUtils.containChinese(installCommand);
                    if (!isWinFileContainZh && isWinCommandContainZh) {
                        installCommand = CommandUtils.fixWinZhCommand(installCommand);
                    }
                    innerCommands.add(installCommand);
                }

                installCommand = SystemTypeCommandUtils.joinCommands(systemType, innerCommands.toArray(new String[0]));

                if (isWinFileContainZh) {
                    installCommand = CommandUtils.fixWinZhCommand(installCommand);
                }

                subInstallCommands.add(installCommand);
            }
        }

        var installerDir = getDefaultInstallDir(systemType);
        if (hasDefaultInstaller && isNewerWindows(systemType)) {
            subInstallCommands.add(String.format(WINDOWS_SPECIAL_DEFAULT_INSTALL_SOFTWARE_START, installerDir));
        }

        if (isWindowsOS) {
            // windows做成脚本文件安装
            var subInstallCommand = SystemTypeCommandUtils.joinCommands(systemType,
                    subInstallCommands.toArray(new String[0]));
            String tmpFile = null;
            try {
                tmpFile = FdfsCommon.uploadContent(subInstallCommand);
            } catch (Exception e) {
                log.error("Upload windows installer script error: ", e);
                installCommands.addAll(subInstallCommands);
            }

            if (tmpFile != null) {
                installCommands.add(String.format(SystemTypeCommandUtils.joinCommands(systemType, WINDOWS_MAKE_SCRIPT_INSTALL),
                        installerDir, StringUtils.joinWith(FILE_URL_SEPARATOR, metadataFileServer, tmpFile), installerDir));
            }
        } else {
            installCommands.addAll(subInstallCommands);
        }

        return installCommands;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDownloadInfo {
        private String filePath;
        private String downloadCommand;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Installer {
        private String name;
        private String file;
        private String installCommand;
        private boolean docker;
        private String copyFile;
    }
}
