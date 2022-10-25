package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaClient;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
class IxiaFlowGenerateService {
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        IxiaClient ixiaClient = new IxiaClient(generatorBO.getServer());
        // 登录
        ixiaClient.login(generatorBO.getUsername(), generatorBO.getPassword());
        // 创建 neighborhood
        ixiaClient.createNeighbor();
        ixiaClient.saveNeighborHood(flowTaskRecordBO.getId());
        // 保存 flows
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
}
