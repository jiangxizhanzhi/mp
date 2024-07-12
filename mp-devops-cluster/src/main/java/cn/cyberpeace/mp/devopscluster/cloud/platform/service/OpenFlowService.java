 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowService.java.
 // File size 1983.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName OpenFlowService
 * @Description 流表监控service
 * @Author qiaokang
 * @Date 2021/7/9 14:57
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
@Deprecated
public class OpenFlowService extends BaseCpService {

    public OpenFlowService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询流表信息
     * @param request
     * @return
     */
    public CloudPlatformPageResult<OpenFlow> page(OpenFlowPageParams request) {
        CloudPlatformResult<List<OpenFlow>> pageResult = new CloudPlatformResult<>(Collections.emptyList());
        pageResult.setCode(BaseCloudPlatformConstant.CODE);
        super.checkResult(pageResult);
        List<OpenFlow> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData()),OpenFlow.class);
        CloudPlatformPageResult<OpenFlow> model = new CloudPlatformPageResult();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

}
