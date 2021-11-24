package cn.cyberpeace.mp.sceneplugincloud.service;

import cn.cyberpeace.mp.base.bo.GroupCountBO;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneConfigElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.CloudPluginSceneConfigElementMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScenePluginCloudSceneConfigService {

    public static final List<String> AVAILABLE_CLOUD_ELEMENT_GROUP_COUNT_FIELDS = Arrays.asList(
            BeanUtils.getBeanGetterFieldNames(
                    BaseCloudPluginSceneElementModel::getVirtualType
            )
    );

    private CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper;

    @Autowired
    public ScenePluginCloudSceneConfigService(CloudPluginSceneConfigElementMapper cloudPluginSceneConfigElementMapper) {
        this.cloudPluginSceneConfigElementMapper = cloudPluginSceneConfigElementMapper;
    }

    public <T> ServiceResult<Map<T, Integer>> groupCountElement(List<String> sceneIds, List<String> nodeIds,
                                                                String groupFieldName) {
        if (!AVAILABLE_CLOUD_ELEMENT_GROUP_COUNT_FIELDS.contains(groupFieldName)) {
            throw new MpRuntimeException(CloudErrorCode.INVALID_GROUP_COUNT_FIELD);
        }

        var condition = new HumanCondition()
                .link(SceneConfigModel.class, CloudPluginSceneConfigElementModel.class)
                .filterLink(SceneConfigModel.class, SceneConfigModel::getId,
                            CloudPluginSceneConfigElementModel.class, CloudPluginSceneConfigElementModel::getScene)
                .currentLink(SceneConfigModel.class)
                .filterEqual(SceneConfigModel::getDeleted, false)
                .currentLink(CloudPluginSceneConfigElementModel.class)
                .selectAs(groupFieldName, GroupCountBO<T>::getTarget)
                .selectAs(Count.f(groupFieldName), GroupCountBO<T>::getCount)
                .groupBy(groupFieldName);
        if (!CollectionUtils.isEmpty(sceneIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getScene, sceneIds.toArray(new String[0]));
        }
        if (!CollectionUtils.isEmpty(nodeIds)) {
            condition.filterIn(BaseCloudPluginSceneElementModel::getSceneElement, nodeIds.toArray(new String[0]));
        }

        List<? extends GroupCountBO> groupCountBOs;
        try {
            groupCountBOs = cloudPluginSceneConfigElementMapper.list(condition, new GroupCountBO<T>().getClass());
        } catch (Exception e) {
            log.error("Group count cloud element on field {} error: {}", groupFieldName, e);
            throw new MpRuntimeException(BaseErrorCode.ERROR, e);
        }

        Map<T, Integer> result = groupCountBOs.stream()
                .collect(Collectors.toMap(groupCountBO -> (T) groupCountBO.getTarget(),
                        groupCountBO -> groupCountBO.getCount()));
        return new ServiceResult<>(result);
    }

}
