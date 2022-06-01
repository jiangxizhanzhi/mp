package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-防火墙
 * @author wanggang
 */
@Service
@Slf4j
public class FirewallService extends BaseOpenStackService {

    public FirewallService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询防火墙
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Firewall> page(String getParams) {
        return super.page(URL_VERSION + "/firewalls" + getParams, Firewall.class);
    }
}
