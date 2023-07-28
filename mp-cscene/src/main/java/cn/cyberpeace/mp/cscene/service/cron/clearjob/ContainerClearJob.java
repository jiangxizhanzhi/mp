package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionPluginSceneMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 */
public class ContainerClearJob extends AbstractClearJob<ContainerGetData> {

    private Container containerClient = new Container();

    public ContainerClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    @Override
    protected Map<String, ContainerGetData> getEid2Node(List<ContainerGetData> containers) {
        Map<String, ContainerGetData> result = new HashMap<>();
        containers.forEach(container -> {
            if (container.getName() != null && container.getId() != null) {
                String eid = this.getEid(container.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, container);
                }
            }
        });
        return result;
    }

    public String getEid(String nodeName) {
        if (nodeName.startsWith("mp_1")) {
            var collectionPluginSceneMapper = BeanCommon.getBean(CollectionPluginSceneMapper.class);
            var collectionPluginScene = collectionPluginSceneMapper.first(new HumanCondition().filterEqual(CollectionPluginSceneModel::getFlowCollector, nodeName));
            if (collectionPluginScene != null) {
                return collectionPluginScene.getScene();
            }
            return null;
        } else {
            return super.getEid(nodeName);
        }
    }

    protected boolean needToClear(String id) {
        if (super.needToClear(id)) {
            return true;
        } else {
            SceneMapper sceneMapper = BeanCommon.getBean(SceneMapper.class);
            var scene = sceneMapper.first(new HumanCondition().filterEqual(SceneModel::getId, id));
            if (scene != null && scene.getEventStatus() == EventStatus.OVER && scene.getEvent() == SceneElementModel.Event.DELETE) {
                return true;
            } else if (scene != null && scene.getEventStatus() == EventStatus.ABNORMAL && scene.getEvent() == SceneElementModel.Event.DELETE) {
                return true;
            } else if (scene != null && scene.getEventStatus() == EventStatus.ABNORMAL && scene.getEvent() == SceneElementModel.Event.CREATE) {
                return true;
            }
            return false;
        }
    }

    @Override
    protected List<ContainerGetData> doQuery(int offset) {
        return containerClient.getAllContainer(LIMIT, offset);
    }

    @Override
    protected void doClear(ContainerGetData data) {
        containerClient.deleteContainerById(data.getId());
    }

    @Override
    protected void forceClear(ContainerGetData data) {
        ForceReleaseResourceUtils.forceDeleteContainer(Arrays.asList(data.getId()));
    }
}
