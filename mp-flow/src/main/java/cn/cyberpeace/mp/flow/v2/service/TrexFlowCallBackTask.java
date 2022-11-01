package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.trex.QueryTaskLastStatusData;
import cn.cyberpeace.mp.flow.v2.client.trex.QueryTaskStatusData;
import cn.cyberpeace.mp.flow.v2.client.trex.TaskLastStatusResponseData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/26
 */
public class TrexFlowCallBackTask implements FlowCallBackTask {
    private FlowTaskRecordBO flowTaskRecordBO;
    private TrexClient trexClient;

    public TrexFlowCallBackTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        this.flowTaskRecordBO = flowTaskRecordBO;
        this.trexClient = new TrexClient(generatorBO.getServer());
    }

    @Override
    public boolean action() {
        // todo
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        FlowTaskCountMapper flowTaskCountMapper = BeanCommon.getBean(FlowTaskCountMapper.class);
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setId(flowTaskRecordBO.getId());

        QueryTaskStatusData queryTaskStatusData = new QueryTaskStatusData();
        List<String> list = new ArrayList<>();
        list.add(flowTaskRecordBO.getId());
        queryTaskStatusData.setTaskIds(list);
        var status = trexClient.queryTaskStatus(queryTaskStatusData);

        recordModel.setStatus(status.get(0).getStatus());
        recordMapper.update(recordModel);
        System.out.println(flowTaskRecordBO.getId() + "状态：" + status.get(0).getStatus());
        // 查询任务状态 包括流量、速率、包数量、tcp数量等
        QueryTaskLastStatusData queryTaskLastStatus = new QueryTaskLastStatusData();
        queryTaskLastStatus.setTaskId(flowTaskRecordBO.getId());
        var taskStatus = trexClient.queryTaskLastStatus(queryTaskLastStatus);
        Map<String, TaskLastStatusResponseData.singleTask> allTaskStatus = new HashMap<>();
        taskStatus.getRows().stream().forEach(t -> {
            allTaskStatus.put(t.getTaskUuid(), t);
        });
        var targetTask = allTaskStatus.get(flowTaskRecordBO.getId());

        if (targetTask != null) {
            FlowTaskCountModel flowTaskCountModel = new FlowTaskCountModel();
            flowTaskCountModel.setFlowId(targetTask.getFlowId());
            flowTaskCountModel.setFlowTaskRecordId(flowTaskRecordBO.getId());
            flowTaskCountModel.setFlowGeneratorId(flowTaskRecordBO.getFlowGeneratorId());
            flowTaskCountModel.setBandwidth(1L);
            flowTaskCountModel.setTcpConnectTry(1L);
            flowTaskCountModel.setPackets(targetTask.getOpackets());
            flowTaskCountModel.setTcp_connect_success(1L);
            // todo : 此处可能有去重问题，暂时忽略
            flowTaskCountMapper.create(flowTaskCountModel);
        }
        if (recordModel.getStatus() == 2) {
            return true;
        } else if (recordModel.getStatus() == 3) {
            return true;
        } else if (recordModel.getStatus() == 4) {
            return true;
        }
        return false;
    }
}
