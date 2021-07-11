package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;


@Slf4j
public abstract class AbstractClearJob<Q> {
    private static final String PREFIX = CloudSingleton.cloudAppConfig.getCpcloudResourceGroup();
    protected static final int OFFSET = 1;
    protected static final int LIMIT = 10;

    protected SceneElementMapper sceneElementMapper;

    public AbstractClearJob(SceneElementMapper sceneElementMapper) {
        this.sceneElementMapper = sceneElementMapper;
    }

    public void action() {
        for (Integer offset = OFFSET; ; offset++) {
            List<Q> nodes = this.doQuery(offset);
            if (CollectionUtils.isEmpty(nodes)) {
                break;
            }
            Map<String, Q> eid2Node = this.getEid2Node(nodes);
            eid2Node.forEach((eid, node) -> {
                try {
                    if (this.needToClear(eid)) {
                        log.info("find one error status node , eid is {},node info is {}", eid, JsonCommon.stringify(node));
                        Integer retryTimes = BeanCommon.getBean(CloudAppConfig.class).getDeleteRetryTimes();
                        boolean success = false;
                        for (int i = 0; i < retryTimes; i++) {
                            try {
                                this.doClear(node);
                                success = true;
                                break;
                            } catch (Exception e) {
                                log.error("failed to delete container at clear job , resource is {} ", JsonCommon.stringify(node), e);
                            }
                        }
                        if (!success) {
                            this.forceClear(node);
                        }
                        log.info("success to clear error status node ,eid is {}", eid);
                    }
                } catch (Exception e) {
                    log.error("failed to execute clear job {} ,eid is {},node info is {}", this.getClass().getName(), eid, JsonCommon.stringify(node), e);
                }
            });
        }
    }

    protected abstract Map<String, Q> getEid2Node(List<Q> nodes);

    protected abstract List<Q> doQuery(int offset);

    protected abstract void doClear(Q data);

    protected boolean needToClear(String id) {
        if (StringUtils.isEmpty(id)) {
            return false;
        }
        var condition = new HumanCondition().filterEqual(SceneElementModel::getId, id);
        List<SceneElementModel> elements = sceneElementMapper.list(condition);
        if (CollectionUtils.isEmpty(elements)) {
            return false;
        }
        SceneElementModel element = elements.get(0);
        if (element.getEvent() == null || element.getEventStatus() == null) {
            return false;
        }
        if (element.getEventStatus() == EventStatus.OVER && element.getEvent() == SceneElementModel.Event.DELETE) {
            return true;
        } else {
            SceneMapper sceneMapper = BeanCommon.getBean(SceneMapper.class);
            var scene = sceneMapper.first(new HumanCondition().filterEqual(SceneModel::getId, element.getScene()));
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

    public String getEid(String nodeName) {
        if (nodeName.startsWith(PREFIX)) {
            String sceneInfo = nodeName.replace(PREFIX + "_", "");
            String[] sp = sceneInfo.split("_");
            if (sp.length > 0) {
                return sp[0];
            }
        }
        if (nodeName.startsWith("TREX_")) {
            String sceneInfo = nodeName.replace("TREX_", "");
            return sceneInfo;
        }
        return null;
    }

    protected void forceClear(Q data) {
    }
}
