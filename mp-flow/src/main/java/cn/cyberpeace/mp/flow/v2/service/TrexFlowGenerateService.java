package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
public class TrexFlowGenerateService {
    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        TrexClient trexClient = new TrexClient();
        // 上传flow配置
        List<TrexClient.FlowResource> flowResources = new ArrayList<>();
        List<TrexClient.ProtocolInfo> protocolInfos = new ArrayList<>();

        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                TrexClient.FlowResource resource = new TrexClient.FlowResource();
                resource.setPcapFile(flow.getPcapFile());
                resource.setFlowId("");
                flowResources.add(resource);
            } else {
                TrexClient.ProtocolInfo protocolInfo = new TrexClient.ProtocolInfo();
                protocolInfos.add(protocolInfo);
            }
        }
        // 网卡已经创建过了，此处不再创建
        var nic = trexClient.createNic();
        // 初始化trex服务
        TrexClient.InitInfo initInfo = new TrexClient.InitInfo();
        initInfo.setMac(flowTaskRecordBO.getTrexMac());
        // todo:taskId如何得到?
        initInfo.setTaskId(flowTaskRecordBO.getFlowGeneratorId());
        trexClient.initTrexServer(initInfo);
        // 上传 pcap
        trexClient.uploadResource(flowResources);
        // 计算负载
        List<TrexClient.LoadInfo> loadInfos = new ArrayList<>();
        trexClient.startTask(protocolInfos, loadInfos);

        return new ServiceResult<>(true);
    }

    private List<TrexClient.LoadInfo> calLoadInfos() {
        return new ArrayList<>();
    }
}
