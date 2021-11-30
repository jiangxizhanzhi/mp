package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.flow.FlowRiskCount;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/30 13:41
 */
@Slf4j
@Service
public class FlowMonitorService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    public FlowRiskCount getFlowRiskCount(String sceneId, String elementId) {
        if (StringUtils.isBlank(sceneId)) {
            throw new MpRuntimeException("sceneId为空");
        }
        List<String> sceneIds = CollectionUtil.toList(sceneId);

        List<String> nodeIds = null;
        if (StringUtils.isNotBlank(elementId)) {
            nodeIds = CollectionUtil.toList(elementId);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now;
        LocalDateTime startTime = now.minusMinutes(1);

        ServiceResult<Map<String, Integer>> mapResult = scenePluginCollectionSceneService.groupCountFlowByLevel(sceneIds, nodeIds, startTime, endTime);
        Map<String, Integer> map = mapResult.getSource();
        // todo
        return null;
    }

}
