package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_OPENFLOW;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * @Description: 流表监控service
 * @Author qiaokang
 * @Date：2021/7/9 14:57
 */
@Service
@Slf4j
public class OpenFlowService extends BaseOpenStackService {

    public OpenFlowService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询流表信息
     * @param request
     * @return
     */
    public CloudPlatformPageModel<OpenFlow> page(OpenFlowPageParams request) {
        CloudPlatformResult<List<OpenFlow>> pageResult = super.sendPost(CPCLOUD_OPENFLOW + URL_VERSION + "/openflow/action", JsonUtils.toUnderlineJSONString(request), CloudPlatformResult.class);
        super.checkResult(pageResult);
        List<OpenFlow> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData()),OpenFlow.class);
        CloudPlatformPageModel<OpenFlow> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

}
