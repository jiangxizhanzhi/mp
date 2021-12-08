package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.FirewallPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.Firewall;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 运维管控-系统运维-防火墙
 * @author wanggang
 */
@Service
@Slf4j
public class FirewallService extends BaseOpenStackServiceImpl {

    public FirewallService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询防火墙
     * @param getParams
     * @return
     */
    public FirewallCloudPlatformModel page(String getParams) {
        FirewallPageResult pageResult = super.sendGet(URL_VERSION + "/firewalls" + getParams, FirewallPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<Firewall> data = pageResult.getData();
        List<Firewall> list = data.getList();
        FirewallCloudPlatformModel model = new FirewallCloudPlatformModel();
        model.setFirewalls(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
