package cn.cyberpeace.mp.sceneplugincloud.config;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualInternet;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CloudAppConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    @Value("${mp.sceneplugincloud.cpcloud_http_pool_size:200}")
    private Integer cpcloudHttpPoolSize;

    @Value("${mp.sceneplugincloud.initiate_pool_size:100}")
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

    @Value("${mp.sceneplugincloud.start_pool_size:10}")
    private Integer startPoolSize;

    @Value("${mp.sceneplugincloud.start_hold_size:200000}")
    private Integer startHoldSize;

    @Value("${mp.sceneplugincloud.stop_pool_size:10}")
    private Integer stopPoolSize;

    @Value("${mp.sceneplugincloud.stop_hold_size:200000}")
    private Integer stopHoldSize;

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

    @Value("${mp.sceneplugincloud.link_status_pool_size:10}")
    private Integer linkStatusPoolSize;

    @Value("${mp.sceneplugincloud.link_status_hold_size:200}")
    private Integer linkStatusHoldSize;

    @Value("${mp.sceneplugincloud.external_net_id:}")
    private String externalNetId;

    @Value("${mp.sceneplugincloud.external_net_cidr:}")
    private String externalNetCidr;

    @Value("${mp.sceneplugincloud.external_net_pool:}")
    private String externalNetPool;

    @Value("${mp.sceneplugincloud.mgmt_name:mgmt}")
    private String mgmtName;

    @Value("${mp.sceneplugincloud.mgmt_net_id:}")
    private String mgmtNetId;

    @Value("${mp.scene.container_help_installer_dir:}")
    private String containerHelpInstallerDir;

    @Value("${mp.scene.remote_connection_token_expire_seconds:7200}")
    private Integer remoteConnectionTokenExpireSeconds;

    @Value("${mp.sceneplugincloud.cloud_heavy_concurrent_delay_enable:true}")
    private Boolean cloudHeavyConcurrentDelayEnable;

    @Value("${mp.sceneplugincloud.cloud_heavy_concurrent_delay:10}")
    private Integer cloudHeavyConcurrentDelay;

    @Value("${mp.sceneplugincloud.ignore_delete_exception:true}")
    private Boolean ignoreDeleteException;

    @Value("${mp.sceneplugincloud.quick_create_docker_resource_metaserver:http://169.254.169.254/installer_upload}")
    private String quickCreateDockerResourceMetaServer;

    @Value("${mp.sceneplugincloud.delete_retry_times:1}")
    private Integer deleteRetryTimes;

    @Value("${mp.sceneplugincloud.default_core_router_name:Router}")
    private String defaultCoreRouterName;

    @Value("${mp.sceneplugincloud.enable_reallocate_ip_ports:false}")
    private Boolean enableReallocateIpPorts;

    @Value("${mp.sceneplugincloud.enable_force_delete:true}")
    private Boolean enableForceDelete;

    @Value("${mp.sceneplugincloud.link_status_enabled:true}")
    private Boolean linkStatusEnabled;

    @Value("${mp.sceneplugincloud.link_status_refresh_period:5}")
    private Integer linkStatusRefreshPeriod;

    @Value("${mp.sceneplugincloud.link_status_cache_seconds:600}")
    private Integer linkStatusCacheSeconds;

    @Value("${mp.sceneplugincloud.public-net-proxy-server:false}")
    private Boolean publicNetProxyServer;

    @Value("${mp.sceneplugincloud.public-net-proxy:false}")
    private Boolean publicNetProxy;

    @Value("${mp.sceneplugincloud.public-net-proxy-ip:}")
    private String publicNetProxyIp;

    @Value("${mp.sceneplugincloud.public-net-proxy-port:}")
    private Integer publicNetProxyPort;

    @Value("${mp.sceneplugincloud.gateway_types:2,3}")
    private String gatewayTypes;

    @Value("${mp.sceneplugincloud.scene_create_install_unzip_apk:true}")
    private Boolean sceneCreateInstallUnzipApk;

    @Value("${mp.sceneplugincloud.dhcp_ip_allocate:true}")
    private Boolean dhcpIpAllocate;

    @Value("${mp.sceneplugincloud.strategy_image_name:strategy-server}")
    private String strategyImageName;

    @Value("${mp.sceneplugincloud.strategy_cpu:2}")
    private Integer strategyCpu;

    @Value("${mp.sceneplugincloud.strategy_ram:2048}")
    private Integer strategyRam;

    @Value("${mp.sceneplugincloud.strategy_disk:40}")
    private Integer strategyDisk;

    @Autowired
    private BaseAppConfig baseAppConfig;

    private List<String> cachedCidrs;

    /**
     * init
     */
    public void init() {
        this.getCidrPool();
    }

    /**
     * getExternalNetPool
     *
     * @return
     */
    public List<AllocationPool> getExternalNetPool() {
        final ArrayList<AllocationPool> allocationPools = new ArrayList<>();
        if (!StringUtils.isEmpty(externalNetPool)) {
            log.info("externalNetPool select first from pool is {}", externalNetPool);
            final String[] ipPools = externalNetPool.split(",");
            for(String ipPool: ipPools ){
                final String[] ips = ipPool.split("-");
                final AllocationPool allocationPool = new AllocationPool(ips[0], ips[1]);
                allocationPools.add(allocationPool);
            }
        }
        return allocationPools;
    }

    /**
     * getCidrPool
     *
     * @return
     */
    public List<String> getCidrPool() {
        if (cachedCidrs == null) {
            List<String> cidrPool = new ArrayList<>();
            for (int cidrA = 172; cidrA < 173; cidrA++) {
                for (int cidrB = 0; cidrB < 255; cidrB++) {
                    for (int cidrC = 0; cidrC < 255; cidrC++) {
                        cidrPool.add(String.format("%d.%d.%d.0/24", cidrA, cidrB, cidrC));
                    }
                }
            }
            cachedCidrs = cidrPool;
        }
        List<String> cidrPool = new ArrayList<>();
        cidrPool.addAll(cachedCidrs);
        return cidrPool;
    }

    /**
     * getExternalNetId
     *
     * @return
     */
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

    /**
     * getExternalNetCidr
     *
     * @return
     */
    public String getExternalNetCidr() {
        if (!StringUtils.isEmpty(externalNetCidr)) {
            return externalNetCidr;
        }

        String netId = getExternalNetId();
        if (StringUtils.isEmpty(netId)) {
            return null;
        }

        try {
            externalNetCidr = new Network().get(netId).getSubnet().getCidr();
        } catch (Exception e) {
            log.error("Try to get external net cidr failed: ", e);
            throw e;
        }

        return externalNetCidr;
    }

    /**
     * getMgmtNetId
     *
     * @return
     */
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
            String message = "Unable to find mgmt network with name `mgmt`";
            log.error("Try to get mgmt net failed: {}", message);
            throw new MpRuntimeException(message);
        }

        return mgmtNetId;
    }

    /**
     * getContainerHelpInstallerDir
     *
     * @return
     */
    public String getContainerHelpInstallerDir() {
        if (!StringUtils.isEmpty(containerHelpInstallerDir)) {
            return containerHelpInstallerDir;
        }

        containerHelpInstallerDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR, baseAppConfig.getResourcesDir(),
                "scene_plugin_cloud/container_installer");
        return containerHelpInstallerDir;
    }

}
