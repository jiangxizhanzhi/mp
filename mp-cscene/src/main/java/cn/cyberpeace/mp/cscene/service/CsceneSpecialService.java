package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneStatBO;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CsceneSpecialService {

    @Autowired
    private CsceneMapper csceneMapper;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    @Data
    public static class CsceneElementStat {
        private String csceneId;
        private Integer elementType;
        private Integer elementTypeCount;
    }

    @Data
    public static class CsceneTestInstanceStat {
        private String csceneId;
        private Integer testInstanceCount;
    }

    public ServiceResult<List<CsceneStatBO>> stat(String[] ids, CsceneContextBO context) {
        if (ids == null || ids.length == 0) {
            return new ServiceResult<>(new ArrayList<>());
        }

        Map<String, CsceneStatBO> statMap = new HashMap<>();
        for (String id : ids) {
            var csceneStat = new CsceneStatBO(id, 0, 0, 0, 0, 0);
            statMap.put(id, csceneStat);
        }

        var elementStatCondition = new HumanCondition()
                .link(CsceneModel.class, SceneConfigElementModel.class)
                .filterLink(CsceneModel.class, CsceneModel::getSceneConfig,
                        SceneConfigElementModel.class, SceneConfigElementModel::getScene)
                .currentLink(CsceneModel.class)
                .filterIn(CsceneModel::getId, ids)
                .groupBy(CsceneModel::getId)
                .selectAs(CsceneModel::getId, CsceneElementStat::getCsceneId)
                .currentLink(SceneConfigElementModel.class)
                .groupBy(SceneConfigElementModel::getType)
                .selectAs(SceneConfigElementModel::getType, CsceneElementStat::getElementType)
                .selectAs(Count.f(SceneConfigElementModel::getType), CsceneElementStat::getElementTypeCount);
        List<CsceneElementStat> csceneElementStats = csceneMapper.list(elementStatCondition, CsceneElementStat.class);
        csceneElementStats.forEach(elementStat -> {
            var stat = statMap.get(elementStat.getCsceneId());
            var elementType = elementStat.getElementType();
            if (elementType.equals(BaseSceneElementModel.Type.SWITCH)) {
                stat.setSwitchCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.ROUTER)) {
                stat.setRouterCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.FIREWALL)) {
                stat.setFirewallCount(elementStat.getElementTypeCount());
            } else if (elementType.equals(BaseSceneElementModel.Type.TERMINAL)) {
                stat.setTerminalCount(elementStat.getElementTypeCount());
            }
        });

        var testSceneStatCondition = new HumanCondition()
                .link(CsceneInstanceModel.class, SceneModel.class)
                .filterLink(CsceneInstanceModel.class, CsceneInstanceModel::getScene,
                        SceneModel.class, SceneModel::getId)
                .currentLink(CsceneInstanceModel.class)
                .filterEqual(CsceneInstanceModel::getType, CsceneInstanceModel.Type.TEST)
                .filterIn(CsceneInstanceModel::getCscene, ids)
                .groupBy(CsceneInstanceModel::getCscene)
                .selectAs(CsceneInstanceModel::getCscene, CsceneTestInstanceStat::getCsceneId)
                .selectAs(Count.f(CsceneInstanceModel::getCscene), CsceneTestInstanceStat::getTestInstanceCount)
                .currentLink(SceneModel.class)
                .filterEqual(SceneModel::getDeleted, false);
        List<CsceneTestInstanceStat> csceneTestInstanceStats = csceneMapper.list(testSceneStatCondition,
                CsceneTestInstanceStat.class);
        csceneTestInstanceStats.forEach(testInstanceStat -> {
            var stat = statMap.get(testInstanceStat.getCsceneId());
            stat.setTestSceneCount(testInstanceStat.getTestInstanceCount());
        });

        return new ServiceResult<>(new ArrayList<>(statMap.values()));
    }
}
