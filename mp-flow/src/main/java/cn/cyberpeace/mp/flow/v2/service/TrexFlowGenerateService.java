package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.trex.FlowResourceData;
import cn.cyberpeace.mp.flow.v2.client.trex.InitInfoData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
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
public class TrexFlowGenerateService {
    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO) {
        TrexClient trexClient = new TrexClient();
        // 上传flow配置
        List<FlowResourceData> flowResources = new ArrayList<>();
        List<TrexClient.ProtocolInfo> protocolInfos = new ArrayList<>();

        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                FlowResourceData resource = new FlowResourceData();
                byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
                resource.setPcapFile(fileByte);
                resource.setFlowId(flow.getFlowId());
                flowResources.add(resource);
            } else {
                TrexClient.ProtocolInfo protocolInfo = new TrexClient.ProtocolInfo();
                protocolInfos.add(protocolInfo);
            }
        }
        // 初始化trex服务
        InitInfoData initInfo = new InitInfoData();
        initInfo.setMac(flowTaskRecordBO.getTrexMac());
        initInfo.setTaskId(flowTaskRecordBO.getId());
        trexClient.initTrexServer(initInfo);

        // 上传 pcap
        trexClient.uploadResource(flowResources);

        // 计算负载
        List<TrexClient.LoadInfo> loadInfos = trexClient.calculateLoad(40, 100, "BINOMIAL_DISTRIBUTION");
        trexClient.startTask(protocolInfos, loadInfos);

        return new ServiceResult<>(true);
    }

    private List<TrexClient.LoadInfo> calLoadInfos() {
        return new ArrayList<>();
    }
}
