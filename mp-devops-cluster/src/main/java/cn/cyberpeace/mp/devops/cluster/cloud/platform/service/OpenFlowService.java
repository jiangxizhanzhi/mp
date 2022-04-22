package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.OpenFlowPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.OpenFlowPageParams;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.constant.BaseConstants;
import cn.cyberpeace.mp.devops.cluster.model.OpenFlowCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.OpenflowActionRequest;
import cn.cyberpeace.mp.devops.cluster.response.OpenflowActionResponse;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public OpenFlowCloudPlatformModel page(OpenFlowPageParams request) {
        OpenFlowPageResult pageResult = super.sendPost(CPCLOUD_OPENFLOW + URL_VERSION + "/openflow/action", JsonUtils.toUnderlineJSONString(request), OpenFlowPageResult.class);
        super.checkResult(pageResult);
        List<OpenFlow> openFlows = pageResult.getData();
        OpenFlowCloudPlatformModel model = new OpenFlowCloudPlatformModel();
        model.setOpenFlows(CollectionUtils.isEmpty(openFlows) ? Lists.newArrayList() : openFlows);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     * 流表操作查询
     * @param request
     * @return
     */
    public OpenflowActionResponse queryOpenFlowAction(OpenflowActionRequest request) {
        OpenflowActionResponse response = new OpenflowActionResponse();
        List<OpenflowActionResponse.Action> actionList = new ArrayList<>();

        try {
            // 查询流表操作
            String res = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_OPENFLOW + BaseCloudPlatformConstant.URL_VERSION +
                    "/openflow/action", JSONObject.toJSONString(request));

            // 解析结果
            JSONObject resultObj = JSONObject.parseObject(res);
            response = JSONObject.toJavaObject(resultObj, OpenflowActionResponse.class);

            // 判断查询结果是否成功
            if (Objects.nonNull(resultObj.getBoolean(BaseConstants.STR_LOWER_SUCCESS)) && resultObj.getBoolean(BaseConstants.STR_LOWER_SUCCESS)) {
                Set<String> keySets = resultObj.keySet();
                // 过滤key为success、request_id的键
                keySets = keySets.stream().filter(key -> !key.equals(BaseConstants.STR_LOWER_SUCCESS) && !key.equals("request_id")).collect(Collectors.toSet());
                keySets.forEach(key -> {
                    // 分别获取各网桥的数据，合并成一个list
                    JSONArray array = resultObj.getJSONArray(key);
                    if (Objects.nonNull(array)) {
                        List<OpenflowActionResponse.Action> brExList = JSONArray.parseArray(array.toJSONString(), OpenflowActionResponse.Action.class);
                        actionList.addAll(brExList);
                    }
                });
            } else {
                log.error("流表操作查询失败：{}", resultObj);
            }
        } catch (Exception e) {
            log.error("流表监控查询异常", e);
        }
        response.setList(actionList);
        response.setTotal(actionList.size());

        return response;
    }

}
