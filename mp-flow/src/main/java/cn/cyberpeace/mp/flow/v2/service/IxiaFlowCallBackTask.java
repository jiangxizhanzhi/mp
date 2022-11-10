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
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/25
 */
@Slf4j
public class IxiaFlowCallBackTask implements FlowCallBackTask {

    private FlowTaskRecordBO flowTaskRecordBO;
    private IxiaSession ixiaSession;
    private Integer runid;

    public IxiaFlowCallBackTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO, Integer runid) {
        this.flowTaskRecordBO = flowTaskRecordBO;
        this.ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        this.runid = runid;
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
        Integer status = 0;
        try {
            var res = ixiaSession.getTestState(getTestStateData);
            if (res != null && res.getProgress() == 100) {
                status = 3;
            } else if (res == null) {
                status = 5;
            } else {
                status = 1;
            }
        } catch (Exception e) {
            log.info("ixia get TEST status fail, runid is {}, error message is {}", this.runid, e.getMessage());
            ixiaSession.close();
        }
        ixiaSession.close();


        recordModel.setStatus(status);
        recordMapper.update(recordModel);

        // 查询任务状态 包括流量、速率、包数量、tcp数量等
//        FlowTaskCountModel flowTaskCountModel = new FlowTaskCountModel();
//        flowTaskCountModel.setFlowId("1");
//        flowTaskCountModel.setFlowTaskRecordId(flowTaskRecordBO.getId());
//        flowTaskCountModel.setFlowGeneratorId(flowTaskRecordBO.getFlowGeneratorId());
//        flowTaskCountModel.setBandwidth(1L);
//        flowTaskCountModel.setTcpConnectTry(Long.valueOf(res.getValues().getTcpClientEstablished()));
//        flowTaskCountModel.setPackets(Long.valueOf(res.getValues().getTcpClientEstablished()));
//        flowTaskCountModel.setTcpConnectSuccess(Long.valueOf(res.getProgress()));
//        flowTaskCountMapper.create(flowTaskCountModel);
        if (recordModel.getStatus() == 3) {
            return true;
        }
        return false;
    }
}