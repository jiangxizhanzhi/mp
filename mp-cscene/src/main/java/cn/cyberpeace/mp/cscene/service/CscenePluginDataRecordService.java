package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneDataMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Data
public class CscenePluginDataRecordService {

    @Resource
    private CollectionPluginSceneDataMapper collectionPluginSceneDataMapper;

    /**
     * getData
     *
     * @param params
     * @param type
     * @param startTime
     * @param endTime
     * @return
     */
    public List<CollectionPluginSceneDataModel> getData(TimeListPageInfoQueryParams params, int type,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        String sceneId = params.getSceneIds().get(0);
        String nodeId = params.getNodeIds().get(0);
        return collectionPluginSceneDataMapper.list(new HumanCondition()
                .filterEqual(CollectionPluginSceneDataModel::getScene, sceneId)
                .filterEqual(CollectionPluginSceneDataModel::getSceneElement, nodeId)
                .filterEqual(CollectionPluginSceneDataModel::getType, type)
                .filterGreaterThan(CollectionPluginSceneDataModel::getCreateTime, startTime)
                .filterLessThan(CollectionPluginSceneDataModel::getCreateTime, endTime)
                .pagination(1, 10)
                .reverseOrderBy(CollectionPluginSceneDataModel::getCreateTime)
        );
    }
}
