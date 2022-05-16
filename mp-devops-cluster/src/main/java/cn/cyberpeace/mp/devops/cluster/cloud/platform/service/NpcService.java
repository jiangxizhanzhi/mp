package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NpcModel;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_URL;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-NPC
 * @author wanggang
 */
@Service
@Slf4j
public class NpcService extends BaseOpenStackService {

    public NpcService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询NPC信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<NpcModel> page(String getParams) {
        CloudPlatformResult<List<NpcModel>> pageResult = super.sendGet(CPCLOUD_URL + "/npc_node" + URL_VERSION + "/node" + getParams, CloudPlatformResult.class);
        super.checkResult(pageResult);
        List<NpcModel> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData()),NpcModel.class);
        CloudPlatformPageModel<NpcModel> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
