package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.GetTestStateData;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaSession;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskCountModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskCountMapper;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
public class IxiaFlowCallBackTask implements FlowCallBackTask {

    private FlowTaskRecordBO flowTaskRecordBO;
    private IxiaSession ixiaSession;
    private Integer runid;
    public IxiaFlowCallBackTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        this.flowTaskRecordBO = flowTaskRecordBO;
        this.ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
    }

    @Override
    public boolean action() {
        // todo
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        FlowTaskCountMapper flowTaskCountMapper = BeanCommon.getBean(FlowTaskCountMapper.class);
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setId(flowTaskRecordBO.getId());


        GetTestStateData getTestStateData = new GetTestStateData();
        getTestStateData.setRunid(this.runid);
        getTestStateData.setRtsgroup("all");
        getTestStateData.setNumDataPoints(1);
        getTestStateData.setNumSeconds(-1);
        ixiaSession.open();
//        var res = ixiaSession.getTestState(getTestStateData);
//        ixiaSession.close();
        recordModel.setStatus(1);
        recordMapper.update(recordModel);

        // 查询任务状态 包括流量、速率、包数量、tcp数量等
        FlowTaskCountModel flowTaskCountModel = new FlowTaskCountModel();
        flowTaskCountModel.setFlowId("1");
        flowTaskCountModel.setFlowTaskRecordId(flowTaskRecordBO.getId());
        flowTaskCountModel.setFlowGeneratorId(flowTaskRecordBO.getFlowGeneratorId());
        flowTaskCountModel.setBandwidth(1L);
        flowTaskCountModel.setTcpConnectTry(1L);
        flowTaskCountModel.setPackets(1L);
        flowTaskCountModel.setTcpConnectSuccess(1L);
//        flowTaskCountMapper.create(flowTaskCountModel);
        return false;
    }
}