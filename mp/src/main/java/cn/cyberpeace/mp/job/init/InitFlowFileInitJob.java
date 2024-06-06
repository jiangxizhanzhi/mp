package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.repository.FlowInfoMapper;
import cn.cyberpeace.mp.flow.repository.FlowProtocolMapper;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.flow.service.impl.FlowPcapServiceImpl;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description:  导入预制流量信息
 * @Author qiaokang
 * @Date：2021/9/1 14:36
 */
@Component
@Slf4j
public class InitFlowFileInitJob {

    @Resource
    private FlowInfoServiceImpl flowInfoService;

    @Resource
    private FlowPcapServiceImpl flowPcapService;

    @Resource
    private FlowInfoMapper flowInfoMapper ;

    @Resource
    private FlowProtocolMapper flowProtocolMapper ;

    @MpInitiate(description = "初始化pcap包", id = "flow_file_init")
    public void run() {
        try {
            // 保证初始化的流量包已经正常导入
            FlowInfoModel flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            if (Objects.nonNull(flowInfoModel)) {
                log.info("=========== no need to import flow file ===========");
                // 即使流量已经存在，也需要判断对应的配置参数信息是否初始化完成
                flowInfoService.checkDefaultFlowScriptParams();
            } else {
                flowInfoService.batchImportFlowInfo();
                flowInfoModel = flowInfoService.selectByTitle("wechat_kZQXz70");
            }
            FlowPcapModel flowPcapModel = flowPcapService.getByFlowId(flowInfoModel.getId());
            if (Objects.nonNull(flowPcapModel) &&
                    !flowInfoModel.getPcapFile().equals(flowPcapModel.getPcapFile())
                    && flowPcapModel.getCreateTime().equals(flowPcapModel.getUpdateTime())) {
                flowInfoService.replacePcapPath();
            } else {
                log.info("=========== no need to init pcap analyse ===========");
            }

            // 保证所有的流量包都分析过流量
            this.checkFlowProtocols();
        } catch (Exception e) {
            log.error("import flow file error，{}", ExceptionUtils.getStackTrace(e));
        }
    }

    private void checkFlowProtocols() {
        int preFiles = flowInfoService.countPreFiles();
        log.info("pre files count {}", preFiles);
        List<FlowInfoModel> modelList = flowInfoMapper.list(new HumanCondition().filterGreaterThan(FlowInfoModel::getPk, preFiles)
                                                                    .orderBy(FlowInfoModel::getPk).pagination(0, 100));
        while (CollectionUtils.isNotEmpty(modelList)) {
            int maxPk = Integer.MAX_VALUE;
            for (FlowInfoModel flowInfoModel : modelList) {
                FlowProtocolModel protocol = flowProtocolMapper.first(new HumanCondition().filterEqual(FlowProtocolModel::getFlowId, flowInfoModel.getId()));
                if (protocol == null) {
                    flowInfoService.initPcapProtocols(flowInfoModel);
                }
                maxPk = flowInfoModel.getPk();
            }
            modelList = flowInfoMapper.list(new HumanCondition().filterGreaterThan(FlowInfoModel::getPk, maxPk)
                                                    .orderBy(FlowInfoModel::getPk).pagination(0, 100));
        }
    }
}
