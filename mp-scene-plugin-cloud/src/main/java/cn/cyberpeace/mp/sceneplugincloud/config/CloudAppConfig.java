package cn.cyberpeace.mp.sceneplugincloud.config;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualInternet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugincloud")
@Data
public class CloudAppConfig {

    @Value("${mp.sceneplugincloud.cpcloud_server:http://127.0.0.1:10009}")
    private String cpcloudServer;

    @Value("${mp.sceneplugincloud.cpcloud_guacamole_server:http://127.0.0.1:9999}")
    private String cpcloudGuacamoleServer;

    @Value("${mp.sceneplugincloud.cpcloud_server_timeout:600000}")
    private Integer cpcloudServerTimeout;

    @Value("${mp.sceneplugincloud.cpcloud_resource_group:MP_SCENE}")
    private String cpcloudResourceGroup;

    @Value("${mp.sceneplugincloud.initiate_pool_size:10}")
    private Integer initiatePoolSize;

    @Value("${mp.sceneplugincloud.initiate_hold_size:200000}")
    private Integer initiateHoldSize;

    @Value("${mp.sceneplugincloud.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.sceneplugincloud.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.sceneplugincloud.recreate_pool_size:10}")
    private Integer recreatePoolSize;

    @Value("${mp.sceneplugincloud.recreate_hold_size:200000}")
    private Integer recreateHoldSize;

    @Value("${mp.sceneplugincloud.restart_pool_size:10}")
    private Integer restartPoolSize;

    @Value("${mp.sceneplugincloud.restart_hold_size:200000}")
    private Integer restartHoldSize;

    @Value("${mp.sceneplugincloud.pause_pool_size:10}")
    private Integer pausePoolSize;

    @Value("${mp.sceneplugincloud.pause_hold_size:200000}")
    private Integer pauseHoldSize;

    @Value("${mp.sceneplugincloud.unpause_pool_size:10}")
    private Integer unpausePoolSize;

    @Value("${mp.sceneplugincloud.unpause_hold_size:200000}")
    private Integer unpauseHoldSize;

    @Value("${mp.sceneplugincloud.suspend_pool_size:10}")
    private Integer suspendPoolSize;

    @Value("${mp.sceneplugincloud.suspend_hold_size:200000}")
    private Integer suspendHoldSize;

    @Value("${mp.sceneplugincloud.resume_pool_size:10}")
    private Integer resumePoolSize;

    @Value("${mp.sceneplugincloud.resume_hold_size:200000}")
    private Integer resumeHoldSize;

    @Value("${mp.sceneplugincloud.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.sceneplugincloud.delete_hold_size:200000}")
    private Integer deleteHoldSize;

    @Value("${mp.sceneplugincloud.external_net_id:}")
    private String externalNetId;

    @Value("${mp.sceneplugincloud.mgmt_name:mgmt}")
    private String mgmtName;

    @Value("${mp.sceneplugincloud.mgmt_net_id:}")
    private String mgmtNetId;

    @Value("${mp.scene.container_help_installer_dir:}")
    private String containerHelpInstallerDir;

    @Autowired
    private BaseAppConfig baseAppConfig;

    public List<String> getCidrPool() {
        List<String> cidrPool = new ArrayList<>();
        IntStream.range(172, 173).forEach(cidrA -> IntStream.range(0, 255)
                .forEach(cidrB -> IntStream.range(0, 255)
                        .forEach(cidrC -> cidrPool.add(String.format("%d.%d.%d.0/24", cidrA, cidrB, cidrC)))));
        return cidrPool;
    }

    public String getExternalNetId() {
        if (!StringUtils.isEmpty(externalNetId)) {
            return externalNetId;
        }

        try {
            externalNetId = new VirtualInternet().create().getExternalNetId();
        } catch (Exception e) {
            log.error("Try to get external net failed: ", e);
            throw e;
        }

        return externalNetId;
    }

    public String getMgmtNetId() {
        if (!StringUtils.isEmpty(mgmtNetId)) {
            return mgmtNetId;
        }

        NetworkData networkData;
        try {
            networkData = new Network().getByName(mgmtName);
        } catch (Exception e) {
            log.error("Try to get mgmt net failed: ", e);
            throw e;
        }

        if (networkData != null) {
            mgmtNetId = networkData.getId();
        } else {
            var message = "Unable to find mgmt network with name `mgmt`";
            log.error("Try to get mgmt net failed: {}", message);
            throw new MpRuntimeException(message);
        }

        return mgmtNetId;
    }

    public String getContainerHelpInstallerDir() {
        if (!StringUtils.isEmpty(containerHelpInstallerDir)) {
            return containerHelpInstallerDir;
        }

        containerHelpInstallerDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(),
                "scene_plugin_cloud/container_installer");
        return containerHelpInstallerDir;
    }

}
