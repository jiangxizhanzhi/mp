package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaSession;
import cn.cyberpeace.mp.flow.v2.client.ixia.PcapFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.ProtocolFlow;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                PcapFlow.FileInfo fileInfo = new PcapFlow.FileInfo();
                //todo 此处为方便测试，用随机值作为上传的pcap包名
                fileInfo.setName(getNumber(15).toString());
                pcapFlow.setFileInfo(fileInfo);
                byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
                pcapFlow.setFile(fileByte);
                pcapFlows.add(pcapFlow);
            } else {
                ProtocolFlow protocolFlow = new ProtocolFlow();
                // flow.getProtocolConfigs().get(0).getConfig() 转换一下
                protocolFlows.add(protocolFlow);
            }
        }
        // todo 此处返回的profileName作为要添加到TEST中的application profile的名
//        String profileName = ixiaSession.createApplicationProfile(protocolFlows);
        // todo neighborhood只能用已有的来创建新的，而且不能配置
//        ixiaSession.addNeighborhood();
        List<String> captureNames = ixiaSession.createCaptureManager(pcapFlows);
        ixiaSession.createTest(neighborhood, "profileName", captureNames);
        String testName = getNumber(15).toString();

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
        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        ixiaSession.open();
        // todo 暂停TEST没参数？等可以测试了看下怎么用
        ixiaSession.pauseTest();
        ixiaSession.close();
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
    //num为随机数字字符串的长度
    public static StringBuilder getNumber(int num){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < num; i++){
            sb.append(random.nextInt(10));
        }
        return sb;
    }

}
