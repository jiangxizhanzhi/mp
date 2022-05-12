package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorService extends BaseOpenStackService {

    public FlavorService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<Flavor> page(String getParams) {
        CloudPlatformPageResult pageResult = super.sendGet(  URL_VERSION + "/flavors" + getParams, CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<Flavor> list =  JSONObject.parseArray(JSONObject.toJSONString(data.getList()), Flavor.class);
        CloudPlatformPageModel<Flavor> model = new CloudPlatformPageModel<>();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
