package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.AddActionData;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaClient;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
class IxiaFlowGenerateService implements IGenerateService {
    @Override
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        IxiaClient ixiaClient = new IxiaClient(generatorBO.getServer());
        // 登录
        ixiaClient.login(generatorBO.getUsername(), generatorBO.getPassword());
        // 创建 neighborhood
        ixiaClient.createNeighbor();
        ixiaClient.saveNeighborHood(flowTaskRecordBO.getId());
        // 保存 flows
        ixiaClient.createSuperflow();
        for (int i = 1; i <= 3; i++) {
            ixiaClient.addflow("plus050", "Server", "Client");
            for (int j = 1; j < 3; j++) {
                AddActionData addActionData = new AddActionData();
                addActionData.setFlowid(i);
                addActionData.setActionid(j);
                addActionData.setType("tls_accept");
                addActionData.setSource("server");
                ixiaClient.addAction(addActionData);
            }
        }
        ixiaClient.saveSuperflow("mp_" + flowTaskRecordBO.getId());
        // 保存 pcap
        // 生成 application profile
        // 创建 test
        // 选择 neighborhood
        // 创建 application simulation
        // 选择 flows
        // 创建 recreate
        // 选择 pcap
        // 执行test
        // 登出
        return new IxiaFlowCallBackTask(flowTaskRecordBO);
    }

    @Override
    public FlowCallBackTask stopFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        return new IxiaFlowCallBackTask(flowTaskRecordBO);
    }

    @Override
    public FlowCallBackTask pauseFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        return null;
    }

    @Override
    public FlowCallBackTask unpauseFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        return null;
    }

    @Override
    public FlowCallBackTask restartFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        return null;
    }
}
