package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.cloud.VirtualMachine;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaSession;
import cn.cyberpeace.mp.flow.v2.client.ixia.PcapFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.ProtocolFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.StopTestData;
import cn.cyberpeace.mp.flow.v2.client.trex.NetworkInfos;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;
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
        // 创建网卡
        // 判断是否需要创建网卡 如果已经有网卡了，不再重复创建
//        boolean needCreateNet = true;
//        String portMac = null;
        String portIp = null;
//        VirtualMachine virtualMachine = new VirtualMachine();
//        var addPortResult = virtualMachine.addPort(flowTaskRecordBO.getTargetResourceId(),"56e0eaed-c3f9-44f0-a964-ab9107a067d1");
        portIp = "172.27.1.55";
//        if (!generatorBO.getNics().isEmpty()) {
//            for (int i = 0; i < generatorBO.getNics().size(); i++) {
//                if (generatorBO.getNics().get(i).getCpCloudNetworkId().equals(flowTaskRecordBO.getTargetNetworkId())) {
//                    needCreateNet = false;
//                    portMac = generatorBO.getNics().get(i).getMac();
//                    portIp = generatorBO.getNics().get(i).getIp();
//                }
//            }
//        }
//        if (needCreateNet) {
//            var flowGeneratorService = BeanCommon.getBean(FlowGeneratorService.class);
//            List<FlowGeneratorBO.Nic> nics = new ArrayList<>();
//            FlowGeneratorBO.Nic nic = new FlowGeneratorBO.Nic();
//            nic.setCpCloudNetworkId(flowTaskRecordBO.getTargetNetworkId());
//            nics.add(nic);
//            var addPortResult = flowGeneratorService.addFlowGeneratorNics(generatorBO.getId(), nics);
//            if (addPortResult != null && !addPortResult.getSource().isEmpty()) {
//                portIp = addPortResult.getSource().get(0).getIp();
//            }
//        }
        ixiaSession.open();
        String strikeListName = ixiaSession.createStrikeList();
        String loadName = ixiaSession.createLoad(ixiaSession.calculateLoad(50, 100, "binomial"));
        String neighborhood = ixiaSession.createNeighborhood(portIp);

        // 取任务详情
        List<ProtocolFlow> protocolFlows = new ArrayList<>();
        List<PcapFlow> pcapFlows = new ArrayList<>();
        List<PcapFlow.RecreateData> recreateDatas = new ArrayList<>();
        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                PcapFlow pcapFlow = new PcapFlow();
                PcapFlow.FileInfo fileInfo = new PcapFlow.FileInfo();
                // todo pcap包上传后的名字？
                fileInfo.setName(TextUtils.uuid());
                pcapFlow.setFileInfo(fileInfo);

                PcapFlow.RecreateData recreateData = new PcapFlow.RecreateData();
                recreateData.setName(fileInfo.getName());
                recreateData.setDelay("00:00:10");
                recreateDatas.add(recreateData);

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
        ixiaSession.createCaptureManager(pcapFlows);
        String testName = ixiaSession.createTest(neighborhood, "profileName", strikeListName, recreateDatas, loadName);

        var res = ixiaSession.startTest(testName);
        ixiaSession.close();
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setId(flowTaskRecordBO.getId());
        recordModel.setIxiaTestName(testName);
        recordModel.setIxiaRunId(res.getRunid());
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        recordMapper.update(recordModel);
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
        return new IxiaFlowCallBackTask(flowTaskRecordBO, generatorBO, res.getRunid());
    }

    @Override
    public FlowCallBackTask stopFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        ixiaSession.open();
        StopTestData stopTestData = new StopTestData();
        stopTestData.setRunid(flowTaskRecordBO.getIxiaRunId());
        ixiaSession.stopTest(stopTestData);
        ixiaSession.close();
        return null;
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
        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        ixiaSession.open();
        StopTestData stopTestData = new StopTestData();
        stopTestData.setRunid(flowTaskRecordBO.getIxiaRunId());
        ixiaSession.stopTest(stopTestData);
        var res = ixiaSession.startTest(flowTaskRecordBO.getIxiaTestName());
        ixiaSession.close();
        FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
        recordModel.setId(flowTaskRecordBO.getId());
        recordModel.setIxiaRunId(res.getRunid());
        FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
        recordMapper.update(recordModel);
        return new IxiaFlowCallBackTask(flowTaskRecordBO, generatorBO, res.getRunid());
    }

    // 秒数转xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) retStr = "0" + Integer.toString(i);
        else retStr = "" + i;
        return retStr;
    }
}
