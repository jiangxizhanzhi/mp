package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.trex.QueryTaskLastStatusData;
import cn.cyberpeace.mp.flow.v2.client.trex.QueryTaskStatusData;
import cn.cyberpeace.mp.flow.v2.client.trex.TaskActionData;
import cn.cyberpeace.mp.flow.v2.client.trex.TaskLastStatusResponseData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description
 * @Author zhanglin
 * @Date 2022/10/26
 */
@Slf4j
public class TrexFlowCallBackTask implements FlowCallBackTask {
    private FlowTaskRecordBO flowTaskRecordBO;
    private TrexClient trexClient;

    private LocalDateTime stopTime;

    public TrexFlowCallBackTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        this.flowTaskRecordBO = flowTaskRecordBO;
        this.trexClient = new TrexClient(generatorBO.getServer());
        if (Objects.nonNull(flowTaskRecordBO.getDuration())) {
            this.stopTime = LocalDateTime.now().plusSeconds(flowTaskRecordBO.getDuration());
        }
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
        // 查询任务状态 包括流量、速率、包数量、tcp数量等
        QueryTaskLastStatusData queryTaskLastStatus = new QueryTaskLastStatusData();
        queryTaskLastStatus.setTaskId(flowTaskRecordBO.getId());
        var taskStatus = trexClient.queryTaskLastStatus(queryTaskLastStatus);
        taskStatus.getRows().forEach(t -> {
            FlowTaskCountModel flowTaskCountModel = new FlowTaskCountModel();
            flowTaskCountModel.setFlowId(t.getFlowId());
            flowTaskCountModel.setFlowTaskRecordId(flowTaskRecordBO.getId());
            flowTaskCountModel.setFlowGeneratorId(flowTaskRecordBO.getFlowGeneratorId());
            flowTaskCountModel.setBandwidth(t.getTxBps());
            flowTaskCountModel.setTcpConnectTry(t.getCpsTotal());
            flowTaskCountModel.setPackets(t.getOpackets());
            flowTaskCountModel.setTcpConnectSuccess(t.getCpsSuccess());
            flowTaskCountMapper.create(flowTaskCountModel);
        });
        if (recordModel.getStatus() == 2 || recordModel.getStatus() == 3 || recordModel.getStatus() == 4) {
            return true;
        }
        if (Objects.nonNull(stopTime) && LocalDateTime.now().compareTo(stopTime) >= 0) {
            TaskActionData taskActionData = new TaskActionData();
            taskActionData.setAction(TaskActionData.Action.STOP);
            taskActionData.setId(flowTaskRecordBO.getId());
            trexClient.doTaskAction(taskActionData);
        }
        return false;
    }
}
