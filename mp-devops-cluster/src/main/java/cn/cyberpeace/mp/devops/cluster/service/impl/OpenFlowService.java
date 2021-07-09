package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.request.OpenflowActionRequest;
import cn.cyberpeace.mp.devops.cluster.response.OpenflowActionResponse;
import cn.cyberpeace.mp.devops.cluster.service.BaseCloudPlatformService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 流表监控service
 * @Autho qiaokang
 * @Date：2021/7/9 14:57
 */
@Service
@Slf4j
public class OpenFlowService extends BaseOpenStackServiceImpl {

    public OpenFlowService(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 流表操作查询
     * @param request
     * @return
     */
    public OpenflowActionResponse queryOpenFlowAction(OpenflowActionRequest request) {
        // 查询流表操作
        String res = super.sendPost(BaseCloudPlatformService.CPCLOUD_OPENFLOW + BaseCloudPlatformService.URL_VERSION +
                "/openflow/action", JSONObject.toJSONString(request));

        // 解析结果
        JSONObject resultObj = JSONObject.parseObject(res);
        OpenflowActionResponse response = JSONObject.toJavaObject(resultObj, OpenflowActionResponse.class);

        List<OpenflowActionResponse.Action> actionList = new ArrayList<>();
        // 分别获取各网桥的数据，合并成一个list
        JSONArray brExArray = resultObj.getJSONArray("br-ex");
        if (Objects.nonNull(brExArray)) {
            List<OpenflowActionResponse.Action> brExList = JSONArray.parseArray(brExArray.toJSONString(), OpenflowActionResponse.Action.class);
            actionList.addAll(brExList);
        }

        JSONArray brIntArray = resultObj.getJSONArray("br-int");
        if (Objects.nonNull(brIntArray)) {
            List<OpenflowActionResponse.Action> brIntList = JSONArray.parseArray(brIntArray.toJSONString(), OpenflowActionResponse.Action.class);
            actionList.addAll(brIntList);
        }

        JSONArray brMirrorArray = resultObj.getJSONArray("br-mirror");
        if (Objects.nonNull(brMirrorArray)) {
            List<OpenflowActionResponse.Action> brMirrorList = JSONArray.parseArray(brMirrorArray.toJSONString(), OpenflowActionResponse.Action.class);
            actionList.addAll(brMirrorList);
        }

        JSONArray brVlanArray = resultObj.getJSONArray("br-vlan");
        if (Objects.nonNull(brVlanArray)) {
            List<OpenflowActionResponse.Action> brVlanList = JSONArray.parseArray(brVlanArray.toJSONString(), OpenflowActionResponse.Action.class);
            actionList.addAll(brVlanList);
        }

        JSONArray ovsVpnArray = resultObj.getJSONArray("ovs-vpn");
        if (Objects.nonNull(ovsVpnArray)) {
            List<OpenflowActionResponse.Action> brOvsVpnList = JSONArray.parseArray(ovsVpnArray.toJSONString(), OpenflowActionResponse.Action.class);
            actionList.addAll(brOvsVpnList);
        }

        response.setActionList(actionList);

        return response;
    }

}
