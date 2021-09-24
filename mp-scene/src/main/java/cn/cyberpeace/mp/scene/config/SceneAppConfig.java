package cn.cyberpeace.mp.scene.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.scene")
@Data
public class SceneAppConfig {

    @Value("${mp.scene.report_pool_size:10}")
    private Integer reportPoolSize;

    @Value("${mp.scene.report_hold_size:200000}")
    private Integer reportHoldSize;

    @Value("${mp.scene.windows_installer_dir:C:\\Users\\Public}")
    private String windowsInstallerDir;

    @Value("${mp.scene.linux_installer_dir:/tmp/tmp_installers}")
    private String linuxInstallerDir;

    // TODO: 迁移到cloud
    @Value("${mp.scene.metadata_file_server:http://169.254.169.254/file")
    private String metadataFileServer;
}
