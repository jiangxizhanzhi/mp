package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendSceneElementRequest;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:19
 */
@Slf4j
@Service
public class HostStateService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    /**
     * 查询单个节点机器当前状态
     *
     * @return
     */
    public List<HostState> getHostStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);


        return null;

    }


    /**
     * 查询单个节点机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getElementHostStateTrend(HostStateTrendSceneElementRequest trendRequest) {
        log.info("{}", trendRequest);
        if (StringUtils.isBlank(trendRequest.getSceneId())) {
            throw new MpRuntimeException("sceneId为空");
        }
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        List<String> elementIds = trendRequest.getElementIds();
        if (CollectionUtil.isEmpty(elementIds)) {
            throw new MpRuntimeException("节点id不能为空");
        }

        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        ServiceResult<List<IntervalSizeBO<LocalDateTime>>> result = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());


        return null;
    }


    /**
     * 查询场景机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getSceneHostStateTrend(HostStateTrendSceneElementRequest trendRequest) {
        log.info("{}", trendRequest);
        if (StringUtils.isBlank(trendRequest.getSceneId())) {
            throw new MpRuntimeException("sceneId为空");
        }
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        ServiceResult<List<IntervalSizeBO<LocalDateTime>>> result = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());

        return null;
    }

}
