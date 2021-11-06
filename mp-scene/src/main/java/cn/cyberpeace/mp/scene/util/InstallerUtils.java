package cn.cyberpeace.mp.scene.util;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.fdfsclient.FastDFSClient;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * linux文件下载
     */
    public static final String LINUX_FILE_DOWNLOAD = "curl -o \"%s\" %s";

    /**
     * linux rpm默认安装
     */
    public static final String LINUX_RPM_DEFAULT_INSTALL_SOFTWARE = "rpm -ivh %s";

    /**
     * linux deb默认安装
     */
    public static final String LINUX_DEB_DEFAULT_INSTALL_SOFTWARE = "apt-get install %s";

    public static final String INSTALLER_REPLACEMENT = "\\{this\\}";

    private static FastDFSClient fileClient;

    @SuppressWarnings({"java:S1144", "java:S3010"})
    private InstallerUtils(FastDFSClient fileClient) {
        InstallerUtils.fileClient = fileClient;
    }

    public static boolean isWindows(String systemType) {
        return CommandUtils.OS_TYPE_WINDOWS.equals(CommandUtils.getOsType(systemType));
    }

    public static boolean isLinux(String systemType) {
        return CommandUtils.OS_TYPE_LINUX.equals(CommandUtils.getOsType(systemType));
    }

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
        if (isWindows(systemType)) {
            return SceneSingleton.sceneAppConfig.getWindowsInstallerDir();
        } else {
            return SceneSingleton.sceneAppConfig.getLinuxInstallerDir();
        }
    }

    public static String getFileUrlBasename(String fileUrl) {
        var parts = fileUrl.split("/");
        return parts[parts.length - 1];
    }

    public static String getFilePathBasename(String filePathUrl) {
        var parts = filePathUrl.split("\\\\");
        parts = parts[parts.length - 1].split("/");
        return parts[parts.length - 1];
    }

    public static String getFileSuffix(String filePath) {
        var parts = filePath.split("\\.");
        return parts[parts.length - 1];
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
        if (isWindows(systemType)) {
            return String.format(CommandUtils.joinCommands(WINDOWS_BASE_DIR_CREATE, systemType), installerDir,
                    installerDir);
        } else {
            return String.format(CommandUtils.joinCommands(LINUX_BASE_DIR_CREATE, systemType), installerDir,
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
            fileName = getFileUrlBasename(fileUrl);
        }
        var installerDir = getDefaultInstallDir(systemType);
        String filePath;
        String downloadCommand;
        if (isWindows(systemType)) {
            filePath = String.format("%s\\%s", installerDir, fileName);
            downloadCommand = String.format(WINDOWS_FILE_DOWNLOAD, filePath, fileUrl);
        } else {
            filePath = String.format("%s/%s", installerDir, fileName);
            downloadCommand = String.format(LINUX_FILE_DOWNLOAD, filePath, fileUrl);
        }

        return new FileDownloadInfo(filePath, downloadCommand);
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
        if (isWindows(systemType)) {
            if (installCommand.contains(INSTALLER_REPLACEMENT)) {
                var fileName = getFilePathBasename(filePath);
                installCommand = installCommand.replace(INSTALLER_REPLACEMENT, fileName);
                return String.format(CommandUtils.joinCommands(WINDOWS_CUSTOM_NORMAL_INSTALL, systemType), installerDir,
                        installCommand);

            } else if (filePathLower.endsWith(".zip") || filePathLower.endsWith(".rar")) {
                var installFolder = TextUtils.uuid();
                return String.format(CommandUtils.joinCommands(WINDOWS_CUSTOM_UNZIP_INSTALL, systemType), installerDir,
                        installFolder, filePath, installerDir, installFolder, installerDir, installFolder,
                        installCommand);
            }
        } else {
            if (installCommand.contains(INSTALLER_REPLACEMENT)) {
                var fileName = getFilePathBasename(filePath);
                installCommand = installCommand.replace(INSTALLER_REPLACEMENT, fileName);
                return String.format(CommandUtils.joinCommands(LINUX_CUSTOM_NORMAL_INSTALL, systemType), installerDir,
                        installCommand);
            } else if (filePathLower.endsWith(".zip")) {
                var installFolder = TextUtils.uuid();
                return String.format(CommandUtils.joinCommands(LINUX_CUSTOM_UNZIP_INSTALL, systemType), installerDir,
                        installFolder, filePath, installerDir, installFolder, installerDir, installFolder, installerDir,
                        installFolder, installerDir, installFolder, installCommand);
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * 获取windows安装工具
     * @return windows安装工具
     */
    private static List<Installer> getWindowsInstallerTools() {
        return new ArrayList<>();
    }

    /**
     * 生成安装命令
     * @param systemType 系统类型
     * @return 安装命令
     */
    public static String generateInstallerInstallCommand(String systemType, List<Installer> installers) {
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
                var installerToolUrl = StringUtils.join(metadataFileServer, installerTool.getFile());
                var fileDownloadInfo = generateFileDownloadCommand(systemType,
                        installerToolUrl, installerTool.getName());
                subInstallCommands.add(fileDownloadInfo.getDownloadCommand());
            }
        }

        boolean isWindowsOS = isWindows(systemType);
        for (var installer : installers) {
            String installerFile = installer.getFile();
            String installCommand = installer.getInstallCommand();
            if (StringUtils.isEmpty(installerFile)) {
                if (StringUtils.isEmpty(installCommand)) {
                    continue;
                }

                boolean isWinCommandContainZh = isWindowsOS && TextUtils.containChinese(installCommand);
                if (isWinCommandContainZh) {
                    installCommand = CommandUtils.fixWinZhCommand(installCommand);
                }

                subInstallCommands.add(installCommand);
            } else {
                var fileName = getFileUrlBasename(installerFile);
                var fileUrl = StringUtils.join(metadataFileServer, installerFile);
                boolean isWinFileContainZh = isWindowsOS && TextUtils.containChinese(fileName);
                String downloadFileName;
                if (isWinFileContainZh) {
                    downloadFileName = generateFilename(TextUtils.uuid(), getFileSuffix(fileName));
                } else {
                    downloadFileName = fileName;
                }
                var fileDownloadInfo = generateFileDownloadCommand(systemType, fileUrl,
                        downloadFileName);
                subInstallCommands.add(fileDownloadInfo.getDownloadCommand());

                var filePath = fileDownloadInfo.getFilePath();
                List<String> innerCommands = new ArrayList<>();
                if (isWinFileContainZh) {
                    innerCommands.add(String.format("ren %s %s", filePath, fileName));
                    filePath = filePath.replace(downloadFileName, fileName);
                }

                if (StringUtils.isEmpty(installCommand)) {
                    innerCommands.add(generateDefaultInstallCommand(systemType, filePath));
                } else {
                    installCommand = generateDefaultCustomInstallCommand(systemType, filePath, installCommand);
                    boolean isWinCommandContainZh = isWindowsOS && TextUtils.containChinese(installCommand);
                    if (!isWinFileContainZh && isWinCommandContainZh) {
                        installCommand = CommandUtils.fixWinZhCommand(installCommand);
                    }
                    innerCommands.add(installCommand);
                }

                installCommand = CommandUtils.joinCommands(innerCommands.toArray(new String[0]), systemType);

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
            var subInstallCommand = CommandUtils.joinCommands(subInstallCommands.toArray(new String[0]),
                    systemType);
            byte[] bytesContent = subInstallCommand.getBytes(StandardCharsets.UTF_8);
            String tmpFile = null;
            try {
                tmpFile = fileClient.uploadFileWithBase64(Base64.encodeBase64String(bytesContent));
            } catch (Exception e) {
                log.error("Upload windows installer script error: ", e);
                installCommands.addAll(subInstallCommands);
            }

            if (tmpFile != null) {
                installCommands.add(String.format(CommandUtils.joinCommands(WINDOWS_MAKE_SCRIPT_INSTALL, systemType),
                        installerDir, StringUtils.join(metadataFileServer, tmpFile), installerDir));
            }
        } else {
            installCommands.addAll(subInstallCommands);
        }

        return CommandUtils.joinCommands(installCommands.toArray(new String[0]), systemType);
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
    }
}
