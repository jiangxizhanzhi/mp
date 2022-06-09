package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 14:05
 */
@Slf4j
@Service
@CacheConfig(cacheManager = SituationCacheConfig.CACHE_MANAGER)
public class CsceneCommonService {

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Cacheable(value = "translate_sceneids")
    public List<String> translateSceneIds(List<String> sceneIds) {
        log.info("before translate: {}", sceneIds);
        List<String> translatedScendIds = new ArrayList<>();
        for (String sceneId : sceneIds) {
            String translatedSceneId = csceneInstanceService.getSceneId(sceneId);
            translatedScendIds.add(translatedSceneId);
        }
        log.info("after translate: {}", translatedScendIds);
        return translatedScendIds;
    }


    /**
     * 根据sceneId和elementIds查询agentNames
     * @param sceneId
     * @param elementIds
     * @return
     */
    @Cacheable(value = "query_agentnames_by_elementids")
    public List<String> getAgentNamesBySceneIdAndElementIds(String sceneId, List<String> elementIds) {
        log.info("hit getAgentNamesBySceneIdAndElementIds...sceneId:{}, elementIds:{}", sceneId, elementIds);
        List<String> sceneIds = CollectionUtil.toList(sceneId);
        List<String> translatedSceneIds = translateSceneIds(sceneIds);
        List<SceneElementBO> elementBOs = scenePluginCollectionSceneService.listProbeEnabledElementBOs(translatedSceneIds.get(0));

        List<String> agentNames = new ArrayList<>();
        if (CollectionUtil.isEmpty(elementIds)) {
            agentNames = elementBOs.stream().map(SceneElementBO::getEid).collect(Collectors.toList());
        } else {
            agentNames = elementBOs.stream().filter(e -> elementIds.contains(e.getId())).map(SceneElementBO::getEid).collect(Collectors.toList());
        }

        return agentNames;
    }


    /**
     * 查询场景是否是开启状态
     * @return
     */
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
