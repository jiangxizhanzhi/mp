package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@CacheConfig(cacheManager = SituationCacheConfig.CACHE_MANAGER)
public class CsceneCommonService {

    @Resource
    private CsceneService csceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Cacheable(value = "translate_sceneids")
    public List<String> translateSceneIds(List<String> sceneIds) {
        List<String> translatedScendIds = new ArrayList<>();
        for (String sceneId : sceneIds) {
            String translatedSceneId = csceneInstanceService.getSceneId(sceneId);
            translatedScendIds.add(translatedSceneId);
        }
        return translatedScendIds;
    }


    @Cacheable(value = "translate_sceneid")
    public String translateSceneId(String sceneId) {
        String translatedSceneId = csceneInstanceService.getSceneId(sceneId);
        return translatedSceneId;
    }

    @Cacheable(value = "translate_scene_config_id")
    public String translateSceneConfigId(String csceneId) {
        var csceneResult = csceneService.get(csceneId).getSource();
        if (csceneResult == null) {
            return null;
        }
        var cscene = csceneResult.getObj();
        return cscene.getSceneConfig();
    }

    
    @Cacheable(value = "query_agentnames_by_scene_and_elementids")
    public List<String> getAgentNamesBySceneIdAndElementIds(String sceneId, List<String> elementIds) {
        String translatedSceneId = translateSceneId(sceneId);
        if (StringUtils.isBlank(translatedSceneId)) {
            return Collections.emptyList();
        }
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneId).getSource();

        List<String> agentNames = new ArrayList<>();
        if (CollectionUtil.isEmpty(elementIds)) {
            agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());
        } else {
            agentNames = elementBOs.stream().filter(e -> elementIds.contains(e.getId())).map(SceneElementBO::getEid).collect(Collectors.toList());
        }

        return agentNames;
    }

    @Cacheable(value = "query_scene_create_time")
    public LocalDateTime getSceneCreateTime(String sceneId) {
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        if (Objects.nonNull(sceneHandler.getScene())) {
            return sceneHandler.getScene().getCreateTime();
        }
        return null;
    }


    
    @Cacheable(value = "get_agentnames_elementids_map")
    public Map<String, String> getAgentNames2ElementIdMap(String sceneId, List<String> elementIds) {
        List<String> sceneIds = CollectionUtil.toList(sceneId);
        List<String> translatedSceneIds = translateSceneIds(sceneIds);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(translatedSceneIds, elementIds);
        Map<String, SceneElementBO> eid2ElementMap = batchSceneHandler.getAllEid2ElementMap();

        Map<String, String> agentNames2ElementId = new HashMap<>();
        eid2ElementMap.forEach((key, elementBO) -> {
            if (Objects.nonNull(elementBO)) {
                if (CollectionUtil.isEmpty(elementIds)) {
                    agentNames2ElementId.put(key, elementBO.getId());
                } else if (elementIds.contains(elementBO.getId())) {
                    agentNames2ElementId.put(key, elementBO.getId());
                }
            }
        });

        return agentNames2ElementId;
    }


    
    @Cacheable(value = "is_cscene_running")
    public boolean isCsceneInstanceRunning(String csceneId) {
        ServiceResult<SceneStateInfo> result = csceneSpecialService.getSceneStateInfo(csceneId);
        SceneStateInfo sceneStateInfo = result.getSource();
        if (Objects.nonNull(sceneStateInfo)) {
            return sceneStateInfo.isRunning();
        }
        return false;
    }

}
