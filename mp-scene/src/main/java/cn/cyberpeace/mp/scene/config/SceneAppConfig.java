package cn.cyberpeace.mp.scene.config;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.PathUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.scene")
@Data
public class SceneAppConfig {

    @Value("${mp.scene.scene_pool_size:100}")
    private Integer scenePoolSize;

    @Value("${mp.scene.scene_hold_size:10000}")
    private Integer sceneHoldSize;

    @Value("${mp.scene.element_pool_size:100}")
    private Integer elementPoolSize;

    @Value("${mp.scene.element_hold_size:10000}")
    private Integer elementHoldSize;

    @Value("${mp.scene.report_pool_size:10}")
    private Integer reportPoolSize;

    @Value("${mp.scene.report_hold_size:200000}")
    private Integer reportHoldSize;

    @Value("${mp.scene.windows_installer_dir:C:\\Users\\Public}")
    private String windowsInstallerDir;

    @Value("${mp.scene.linux_installer_dir:/tmp/tmp_installers}")
    private String linuxInstallerDir;

    @Value("${mp.scene.metadata_file_server:http://169.254.169.254/file}")
    private String metadataFileServer;

    @Value("${mp.scene.windows_help_installer_dir:}")
    private String windowsHelpInstallerDir;

    @Value("${mp.scene.scene_handler_realtime_update:true}")
    private Boolean sceneHandlerRealtimeUpdate;

    @Value("${mp.scene.scene_process_save_realtime:true}")
    private Boolean sceneProcessSaveRealtime;

    @Value("${mp.scene.element_process_save_realtime:true}")
    private Boolean elementProcessSaveRealtime;

    @Value("${mp.scene.dhcp_control:true}")
    private Boolean dhcpControl;

    @Autowired
    private BaseAppConfig baseAppConfig;

    public String getWindowsHelpInstallerDir() {
        if (!StringUtils.isEmpty(windowsHelpInstallerDir)) {
            return windowsHelpInstallerDir;
        }

        windowsHelpInstallerDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(),
                "scene/windows_installer");
        return windowsHelpInstallerDir;
    }

}
