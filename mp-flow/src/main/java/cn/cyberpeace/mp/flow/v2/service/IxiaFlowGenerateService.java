package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.cloud.VirtualMachine;
import cn.cyberpeace.mp.flow.v2.client.ixia.CreateTestBO;
import cn.cyberpeace.mp.flow.v2.client.ixia.IxiaSession;
import cn.cyberpeace.mp.flow.v2.client.ixia.PcapFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.ProtocolFlow;
import cn.cyberpeace.mp.flow.v2.client.ixia.StopTestData;
import cn.cyberpeace.mp.flow.v2.client.trex.NetworkInfos;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskRecordModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowTaskRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Slf4j
@Service
class IxiaFlowGenerateService implements IGenerateService {
    private static final Integer DURATION = 50;

    @Override
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {

        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        String testName = null;
        CreateTestBO createTestBO = new CreateTestBO();
        // 创建网卡
        // 判断是否需要创建网卡 如果已经有网卡了，不再重复创建
        boolean needCreateNet = true;
        String portIp = null;
        if (!generatorBO.getNics().isEmpty()) {
            for (int i = 0; i < generatorBO.getNics().size(); i++) {
                if (generatorBO.getNics().get(i).getResourceId().equals(flowTaskRecordBO.getTargetResourceId())) {
                    needCreateNet = false;
                    portIp = generatorBO.getNics().get(i).getIp();
                }
            }
        }
        if (needCreateNet) {
            var flowGeneratorService = BeanCommon.getBean(FlowGeneratorService.class);
            List<FlowGeneratorBO.Nic> nics = new ArrayList<>();
            FlowGeneratorBO.Nic nic = new FlowGeneratorBO.Nic();
            nic.setCpCloudNetworkId(flowTaskRecordBO.getTargetNetworkId());
            nic.setResourceId(flowTaskRecordBO.getTargetResourceId());
            nics.add(nic);
            var addPortResult = flowGeneratorService.addFlowGeneratorNics(generatorBO.getId(), nics);
            if (addPortResult != null && !addPortResult.getSource().isEmpty()) {
                portIp = addPortResult.getSource().get(0).getIp();
            }
        }

        // 登录
        ixiaSession.open();
        try {
            // 取任务详情
            List<String> stringList = new ArrayList<>();
            List<ProtocolFlow> protocolFlows = new ArrayList<>();
            List<PcapFlow> pcapFlows = new ArrayList<>();
            List<PcapFlow.RecreateData> recreateDatas = new ArrayList<>();
            for (var flow : flowTaskRecordBO.getFlows()) {
                if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                    if (flow.getSupportEvasion()) {
                        stringList.add(flow.getPcapFile());
                    } else {
                        PcapFlow pcapFlow = new PcapFlow();
                        PcapFlow.FileInfo fileInfo = new PcapFlow.FileInfo();
                        // todo pcap包上传后的名字？
                        fileInfo.setName(TextUtils.uuid());
                        pcapFlow.setFileInfo(fileInfo);

                        PcapFlow.RecreateData recreateData = new PcapFlow.RecreateData();
                        recreateData.setName(fileInfo.getName());
                        recreateData.setDelay(secToTime(flow.getDelay()));
                        recreateDatas.add(recreateData);

                        byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
                        pcapFlow.setFile(fileByte);
                        pcapFlows.add(pcapFlow);
                    }
                } else {
                    ProtocolFlow protocolFlow = new ProtocolFlow();
                    // flow.getProtocolConfigs().get(0).getConfig() 转换一下
                    protocolFlows.add(protocolFlow);
                }
            }
            createTestBO.setRecreateDatas(recreateDatas);
            // 攻击流量
//            String strikeListName = ixiaSession.createStrikeList(stringList);
            if (stringList != null && !stringList.isEmpty()) {
                createTestBO.setStrikeEnable(true);
                createTestBO.setStrikeListName(ixiaSession.createStrikeList(stringList));
            } else {
                createTestBO.setStrikeEnable(false);
            }
            if (!pcapFlows.isEmpty()) {
                var flowLoadService = BeanCommon.getBean(FlowLoadService.class);
                createTestBO.setLoadName(ixiaSession.createLoad(flowLoadService.calculateLoad(DURATION, flowTaskRecordBO.getBandwidth(), flowTaskRecordBO.getLoadMode())));
                ixiaSession.createCaptureManager(pcapFlows);
                createTestBO.setRecreateEnable(true);
            } else {
                createTestBO.setRecreateEnable(false);
            }
            // 计算负载
//            String loadName = ixiaSession.createLoad(ixiaSession.calculateLoad(DURATION, flowTaskRecordBO.getBandwidth(), flowTaskRecordBO.getLoadMode()));
            // 上传pcap
            ixiaSession.createCaptureManager(pcapFlows);
            // 构建neighborhood
//            String neighborhood = ixiaSession.createNeighborhood(portIp);
            createTestBO.setNeighborhood(ixiaSession.createNeighborhood(portIp));
            // todo 此处返回的profileName作为要添加到TEST中的application profile的名
//        String profileName = ixiaSession.createApplicationProfile(protocolFlows);
            // 创建TEST
//            String attackProfile = "Default evasion settings";
            createTestBO.setAttackProfile("Default evasion settings");
            testName = ixiaSession.createTest(createTestBO);
            // 启动TEST
            var res = ixiaSession.startTest(testName);
            FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
            recordModel.setId(flowTaskRecordBO.getId());
            recordModel.setIxiaTestName(testName);
            recordModel.setIxiaRunId(res.getRunid());
            FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
            recordMapper.update(recordModel);
            return new IxiaFlowCallBackTask(flowTaskRecordBO, generatorBO, res.getRunid());
        } catch (Exception e) {
            log.info("ixia start TEST fail, TEST name is {}, error message is {}", testName, e.getMessage());
        } finally {
            ixiaSession.close();
        }
        ixiaSession.close();
        return null;
    }

    @Override
    public FlowCallBackTask stopFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        IxiaSession ixiaSession = new IxiaSession(generatorBO.getServer(), generatorBO.getUsername(), generatorBO.getPassword());
        ixiaSession.open();
        try {
            StopTestData stopTestData = new StopTestData();
            stopTestData.setRunid(flowTaskRecordBO.getIxiaRunId());
            ixiaSession.stopTest(stopTestData);
        } catch (Exception e) {
            log.info("ixia stop TEST fail, runid is {}, error message is {}", flowTaskRecordBO.getIxiaRunId(), e.getMessage());
        } finally {
            ixiaSession.close();
        }
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
        try {
            StopTestData stopTestData = new StopTestData();
            stopTestData.setRunid(flowTaskRecordBO.getIxiaRunId());
            ixiaSession.stopTest(stopTestData);
            var res = ixiaSession.startTest(flowTaskRecordBO.getIxiaTestName());
            FlowTaskRecordModel recordModel = new FlowTaskRecordModel();
            recordModel.setId(flowTaskRecordBO.getId());
            recordModel.setIxiaRunId(res.getRunid());
            FlowTaskRecordMapper recordMapper = BeanCommon.getBean(FlowTaskRecordMapper.class);
            recordMapper.update(recordModel);
            return new IxiaFlowCallBackTask(flowTaskRecordBO, generatorBO, res.getRunid());
        } catch (Exception e) {
            log.info("ixia restart TEST fail, ixia test name is {}, error message is {}", flowTaskRecordBO.getIxiaTestName(), e.getMessage());
        } finally {
            ixiaSession.close();
        }
        ixiaSession.close();
        return null;
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
