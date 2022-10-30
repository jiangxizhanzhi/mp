package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaSession;
import cn.cyberpeace.mp.flow.v2.client.ixia.PcapFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.ProtocolFlow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
class IxiaFlowGenerateService implements IGenerateService {

    @Override
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {

        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        ixiaSession.open();
        String neighborhood = ixiaSession.createNeighborhood();

        // 取任务详情
        List<ProtocolFlow> protocolFlows = new ArrayList<>();
        List<PcapFlow> pcapFlows = new ArrayList<>();
        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                PcapFlow pcapFlow = new PcapFlow();
                pcapFlow.setName(flow.getName());
                pcapFlows.add(pcapFlow);
            } else {
                ProtocolFlow protocolFlow = new ProtocolFlow();
                // flow.getProtocolConfigs().get(0).getConfig() 转换一下
                protocolFlows.add(protocolFlow);
            }
        }
        String profileName = ixiaSession.createApplicationProfile(protocolFlows);
        List<String> captureNames = ixiaSession.createCaptureManager(pcapFlows);
        String testName = ixiaSession.createTest(neighborhood, profileName, captureNames);
        ixiaSession.startTest(testName);
        ixiaSession.close();

//        IxiaClient ixiaClient = new IxiaClient(generatorBO.getServer());
//        // 登录
//        ixiaClient.login(generatorBO.getUsername(), generatorBO.getPassword());
//        // 创建 neighborhood
//        ixiaClient.createNeighbor();
//        ixiaClient.saveNeighborHood(flowTaskRecordBO.getId());
//        // 保存 flows
//        ixiaClient.createSuperflow();
//        for (int i = 1; i <= 3; i++) {
//            ixiaClient.addflow("plus050", "Server", "Client");
//            for (int j = 1; j < 3; j++) {
//                AddActionData addActionData = new AddActionData();
//                addActionData.setFlowid(i);
//                addActionData.setActionid(j);
//                addActionData.setType("tls_accept");
//                addActionData.setSource("server");
//                ixiaClient.addAction(addActionData);
//            }
//        }
//        ixiaClient.saveSuperflow("mp_" + flowTaskRecordBO.getId());
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
