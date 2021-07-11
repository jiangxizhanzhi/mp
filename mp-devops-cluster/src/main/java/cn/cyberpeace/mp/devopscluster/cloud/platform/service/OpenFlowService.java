package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_OPENFLOW;
import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;


@Service
@Slf4j
public class OpenFlowService extends BaseCpService {

    public OpenFlowService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<OpenFlow> page(OpenFlowPageParams request) {
        CloudPlatformResult<List<OpenFlow>> pageResult = super.sendPost(CPCLOUD_OPENFLOW + URL_VERSION + "/openflow/action", JsonUtils.toUnderlineJSONString(request), CloudPlatformResult.class);
        super.checkResult(pageResult);
        List<OpenFlow> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData()),OpenFlow.class);
        CloudPlatformPageResult<OpenFlow> model = new CloudPlatformPageResult();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

}
