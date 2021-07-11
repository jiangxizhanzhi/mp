package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.NpcModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class NpcService extends BaseCpService {

    public NpcService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<NpcModel> page(String getParams) {
        CloudPlatformResult<List<NpcModel>> pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_URL + "/npc_node" + BaseCloudPlatformConstant.URL_VERSION + "/node" + getParams, CloudPlatformResult.class);
        super.checkResult(pageResult);
        List<NpcModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData()),NpcModel.class);
        CloudPlatformPageResult<NpcModel> model = new CloudPlatformPageResult();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
