 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NpcService.java.
 // File size 1897.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName NpcService
 * @Description 运维管控-系统运维-网络实例管理-NPC
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
@Deprecated
public class NpcService extends BaseCpService {

    public NpcService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询NPC信息
     * @param getParams
     * @return
     */
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
