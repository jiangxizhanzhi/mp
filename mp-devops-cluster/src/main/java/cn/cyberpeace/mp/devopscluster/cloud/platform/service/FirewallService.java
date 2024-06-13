package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 运维管控-系统运维-防火墙
 * @author wanggang
 */
@Service
@Slf4j
public class FirewallService extends BaseCpService {

    public FirewallService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询防火墙
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Firewall> page(String getParams) {
        return new CloudPlatformPageResult<>(Collections.emptyList());
    }


}
