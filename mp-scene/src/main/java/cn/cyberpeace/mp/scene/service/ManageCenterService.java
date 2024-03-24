package cn.cyberpeace.mp.scene.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.config.MultiSceneConfig;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import cn.cyberpeace.mp.scene.model.CyberRangeRegisterModel;
import cn.cyberpeace.mp.scene.repository.CyberRangeRegisterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageCenterService {

    private final CyberRangeRegisterMapper cyberRangeRegisterMapper;
    private final MultiSceneConfig multiSceneConfig;

    public void receiveRegisterOrHeartbeat(MultiSceneRegisterBo multiSceneRegisterBo) {
        List<CyberRangeRegisterModel> cyberRangeRegisterModelList =
                cyberRangeRegisterMapper.list(new HumanCondition().filterEqual(CyberRangeRegisterModel::getRangeTag, multiSceneRegisterBo));

        if (CollectionUtils.isEmpty(cyberRangeRegisterModelList)) {
            cyberRangeRegisterMapper.create(multiSceneRegisterBo.toModel());
        } else {
            CyberRangeRegisterModel currentCyberRangeModel = cyberRangeRegisterModelList.get(0);
            currentCyberRangeModel.setRangeStatus(CyberRangeRegisterModel.RangeStatus.ONLINE);
            currentCyberRangeModel.setRangeIp(multiSceneRegisterBo.getRangeIp());
            currentCyberRangeModel.setRangePort(multiSceneRegisterBo.getRangePort());
            cyberRangeRegisterMapper.update(currentCyberRangeModel);
        }
    }

    public List<MultiSceneRegisterBo> getAllOnlineCyberRange() {
        List<CyberRangeRegisterModel> cyberRangeRegisterModelList =
                cyberRangeRegisterMapper.list(new HumanCondition().filterEqual(CyberRangeRegisterModel::getRangeStatus, CyberRangeRegisterModel.RangeStatus.ONLINE));
        if (CollectionUtils.isEmpty(cyberRangeRegisterModelList)) {
            return Collections.emptyList();
        } else {
            return cyberRangeRegisterModelList.stream().map(MultiSceneRegisterBo::fromModel).collect(Collectors.toList());
        }
    }

    public List<MultiSceneRegisterBo> getAllOnlineCyberRangeWithoutSelf() {
        List<MultiSceneRegisterBo> allOnlineRange = getAllOnlineCyberRange();
        if (CollectionUtils.isEmpty(allOnlineRange)) {
            return Collections.emptyList();
        } else {
            return allOnlineRange.stream().filter(range -> !range.getRangeIp().equals(multiSceneConfig.getCyberRangeIp())).collect(Collectors.toList());
        }

    }

    public MultiSceneRegisterBo getTargetCyberRangeBo(String rangeTag) {
        List<CyberRangeRegisterModel> cyberRangeRegisterModelList =
                cyberRangeRegisterMapper.list(new HumanCondition().filterEqual(CyberRangeRegisterModel::getRangeStatus,
                CyberRangeRegisterModel.RangeStatus.ONLINE).filterEqual(CyberRangeRegisterModel::getRangeTag, rangeTag));
        if (CollectionUtils.isEmpty(cyberRangeRegisterModelList)) {
            throw new MpRuntimeException(String.format("分靶场%s已下线", rangeTag));
        } else {
            return MultiSceneRegisterBo.fromModel(cyberRangeRegisterModelList.get(0));
        }
    }

    public void scanNotHeartBeatCyberRange(long scanIntervalSeconds) {

        LocalDateTime heartBeatNeedBiggerTime = LocalDateTime.now().minusSeconds(scanIntervalSeconds/2);

        List<CyberRangeRegisterModel> cyberRangeRegisterModelList = cyberRangeRegisterMapper.list(new HumanCondition().filterEqual(CyberRangeRegisterModel::getRangeStatus, CyberRangeRegisterModel.RangeStatus.ONLINE)
                .filterLessThan(CyberRangeRegisterModel::getUpdateTime, heartBeatNeedBiggerTime));
        if (CollectionUtils.isNotEmpty(cyberRangeRegisterModelList)) {
            cyberRangeRegisterModelList.forEach(model -> model.setRangeStatus(CyberRangeRegisterModel.RangeStatus.OFFLINE));
            cyberRangeRegisterMapper.bulkUpdate(cyberRangeRegisterModelList);
        }
    }
}
