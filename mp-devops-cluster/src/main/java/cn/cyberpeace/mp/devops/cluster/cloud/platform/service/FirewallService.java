package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Firewall;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public FirewallCloudPlatformModel page(String getParams) {
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + "/firewalls" + getParams, CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData<Firewall> data = pageResult.getData();
        List<Firewall> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()), Firewall.class);
        FirewallCloudPlatformModel model = new FirewallCloudPlatformModel();
        model.setFirewalls(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
