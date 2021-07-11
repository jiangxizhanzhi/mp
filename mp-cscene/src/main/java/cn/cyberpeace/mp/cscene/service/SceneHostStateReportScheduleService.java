package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.report.bo.CollectedCsceneElementCommonBO;
import cn.cyberpeace.mp.cscene.report.rabbitmq.RabbitmqCsceneReporter;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.action.CsceneCallAction;
import cn.cyberpeace.mp.cscene.service.taskpool.CsceneReportTaskPool;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SceneHostStateReportScheduleService {
    @Resource
    private CsceneInstanceService csceneInstanceService;
    @Resource
    private CsceneCommonService csceneCommonService;
    @Resource
    private HostStateService hostStateService;
    @Resource
    private RabbitmqCsceneReporter csceneReporter;

    
    public static final long REFRESH_RATE = 2;

    private static final Set<String> csceneInstanceIds = new ConcurrentHashSet<>();

    public static void addCsceneInstance(String csceneInstanceId) {
        csceneInstanceIds.add(csceneInstanceId);
    }

    public static void removeCsceneInstance(String csceneInstanceId) {
        csceneInstanceIds.remove(csceneInstanceId);
    }

    @Scheduled(initialDelay = REFRESH_RATE, fixedRate = REFRESH_RATE, timeUnit = TimeUnit.MINUTES)
    public void scheduleSceneHostStateReport() {
        for (String csceneInstanceId : new ArrayList<>(csceneInstanceIds)) {
            try {
                this.reportCsceneElement(csceneInstanceId);
            } catch (Exception e) {
                log.error("report cscene element error,csceneInstanceId is:{}", csceneInstanceId, e);
            }
        }
    }

    private void reportCsceneElement(String csceneInstanceId) {
        var csceneInstanceModel = csceneInstanceService.get(csceneInstanceId).getSource().getObj();
        if (StringUtils.isNotEmpty(csceneInstanceModel.getPassThrough())) {
            SysContextHolder.setPassThroughMap(JsonCommon.parse(csceneInstanceModel.getPassThrough(), new TypeReference<Map<String, Object>>() {
            }));
        }
        String sceneId = csceneInstanceModel.getScene();
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        if (sceneHandler.getElementMap().isEmpty()) {
            return;
        }
        List<HostState> hostStates = new ArrayList<>();
        Map<String, String> elementEid2Id = sceneHandler.getElementMap().values().stream().collect(Collectors.toMap(e -> e.getEid(), e -> e.getId()));
        var agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(csceneInstanceId, null);
        if (CollectionUtils.isNotEmpty(agentNames)) {
            AgentNameBasedRequest agentNameBasedRequest = new AgentNameBasedRequest();
            agentNameBasedRequest.setAgentNames(agentNames);
            agentNameBasedRequest.setPageSize(1);

            Map<String, List<HostState>> map = hostStateService.mapHostStatesByAgentName(agentNameBasedRequest);
            map.forEach((k, v) -> {
                var hostState = v.get(0);
                hostState.setElementId(elementEid2Id.get(k));
                hostState.setSceneId(sceneId);
                hostStates.add(hostState);
            });
        }
        CollectedCsceneElementCommonBO<List<HostState>> csceneElementCommon = new CollectedCsceneElementCommonBO<>();
        csceneElementCommon.setCsceneId(csceneInstanceId);
        csceneElementCommon.setCallAction(CsceneCallAction.NODE_HOST_STATE);
        csceneElementCommon.setData(hostStates);
        CsceneReportTaskPool.getInstance().executeTask(() -> csceneReporter.reportElementCommon(csceneElementCommon));
    }
}
