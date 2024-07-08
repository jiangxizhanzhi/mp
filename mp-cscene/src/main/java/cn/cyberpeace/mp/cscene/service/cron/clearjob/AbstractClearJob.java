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

/**
 * 清理任务的抽象类，提供清理逻辑的基本框架。
 * 具体的清理任务需要继承这个类，并实现其中的抽象方法。
 */
@Slf4j
public abstract class AbstractClearJob<Q> {
    // 资源组的前缀，用于识别资源归属
    private static final String PREFIX = CloudSingleton.cloudAppConfig.getCpcloudResourceGroup();
    // 查询偏移量的起始值
    protected static final int OFFSET = 1;
    // 每次查询的数量限制
    protected static final int LIMIT = 10;

    // 场景元素Mapper，用于数据库操作
    protected SceneElementMapper sceneElementMapper;

    /**
     * 构造函数，传入场景元素的Mapper接口。
     *
     * @param sceneElementMapper 场景元素的数据库操作接口
     */
    public AbstractClearJob(SceneElementMapper sceneElementMapper) {
        this.sceneElementMapper = sceneElementMapper;
    }

    /**
     * 清理任务的主要执行方法。
     * 通过查询需要清理的资源，然后逐个清理它们。
     */
    public void action() {
        // 通过偏移量循环查询资源，直到没有更多资源
        for (Integer offset = OFFSET; ; offset++) {
            List<Q> nodes = this.doQuery(offset);
            // 如果查询结果为空，结束循环
            if (CollectionUtils.isEmpty(nodes)) {
                break;
            }
            // 将查询结果映射为ID到节点的映射，方便后续处理
            Map<String, Q> eid2Node = this.getEid2Node(nodes);
            for (Map.Entry<String, Q> entry : eid2Node.entrySet()) {
                String eid = entry.getKey();
                Q node = entry.getValue();
                try {
                    // 判断是否需要清理该节点
                    if (this.needToClear(eid)) {
                        // 日志记录需要清理的节点信息
                        log.info("find one error status node , eid is {},node info is {}", eid, JsonCommon.stringify(node));
                        // 尝试清理节点，根据配置的重试次数进行重试
                        Integer retryTimes = BeanCommon.getBean(CloudAppConfig.class).getDeleteRetryTimes();
                        boolean success = false;
                        for (int i = 0; i < retryTimes; i++) {
                            try {
                                this.doClear(node);
                                success = true;
                                break;
                            } catch (Exception e) {
                                // 记录清理失败的日志
                                log.error("failed to delete container at clear job , resource is {} ", JsonCommon.stringify(node), e);
                            }
                        }
                        // 如果所有尝试都失败，调用强制清理方法
                        if (!success) {
                            this.forceClear(node);
                        }
                        // 记录清理成功的日志
                        log.info("success to clear error status node ,eid is {}", eid);
                    }
                } catch (Exception e) {
                    // 记录执行清理任务过程中出现的异常
                    log.error("failed to execute clear job {} ,eid is {},node info is {}", this.getClass().getName(), eid, JsonCommon.stringify(node), e);
                }
            }
        }
    }

    /**
     * 根据查询结果，将节点ID映射到节点对象。
     *
     * @param nodes 查询结果列表
     * @return ID到节点的映射
     */
    protected abstract Map<String, Q> getEid2Node(List<Q> nodes);

    /**
     * 执行具体的查询操作，根据偏移量查询需要清理的资源。
     *
     * @param offset 查询偏移量
     * @return 查询结果列表
     */
    protected abstract List<Q> doQuery(int offset);

    /**
     * 对特定的节点执行清理操作。
     *
     * @param data 需要清理的节点对象
     */
    protected abstract void doClear(Q data);

    /**
     * 判断特定ID的节点是否需要清理。
     *
     * @param id 节点的ID
     * @return 是否需要清理
     */
    protected boolean needToClear(String id) {
        // 如果ID为空，直接返回不需要清理
        if (StringUtils.isEmpty(id)) {
            return false;
        }
        // 根据ID查询节点信息
        HumanCondition condition = new HumanCondition().filterEqual(SceneElementModel::getId, id);
        List<SceneElementModel> elements = sceneElementMapper.list(condition);
        // 如果节点信息不存在，返回不需要清理
        if (CollectionUtils.isEmpty(elements)) {
            return false;
        }
        // 获取查询到的第一个节点
        SceneElementModel element = elements.get(0);
        // 如果节点的事件状态为结束且事件为删除，则直接返回需要清理
        if (element.getEventStatus() == EventStatus.OVER && element.getEvent() == SceneElementModel.Event.DELETE) {
            return true;
        } else {
            // 查询节点所属的场景信息
            SceneMapper sceneMapper = BeanCommon.getBean(SceneMapper.class);
            SceneModel scene = sceneMapper.first(new HumanCondition().filterEqual(SceneModel::getId, element.getScene()));
            // 根据场景的事件状态和事件类型判断是否需要清理
            if (scene != null && (scene.getEventStatus() == EventStatus.OVER || scene.getEventStatus() == EventStatus.ABNORMAL)
                    && scene.getEvent() == SceneElementModel.Event.DELETE) {
                return true;
            } else if (scene != null && scene.getEventStatus() == EventStatus.ABNORMAL && scene.getEvent() == SceneElementModel.Event.CREATE) {
                return true;
            }
            // 如果不符合上述条件，返回不需要清理
            return false;
        }
    }

    /**
     * 从节点名称中提取出场景元素ID。
     *
     * @param nodeName 节点名称
     * @return 场景元素ID，如果无法提取则返回null
     */
    public String getEid(String nodeName) {
        // 如果节点名称以资源组前缀开头，尝试提取ID
        if (nodeName.startsWith(PREFIX)) {
            String sceneInfo = nodeName.replace(PREFIX + "_", "");
            String[] sp = sceneInfo.split("_");
            // 如果提取成功，返回ID
            if (sp.length > 0) {
                return sp[0];
            }
        }
        // 如果节点名称以"TREX_"开头，尝试提取ID
        if (nodeName.startsWith("TREX_")) {
            String sceneInfo = nodeName.replace("TREX_", "");
            return sceneInfo;
        }
        // 如果都不符合，返回null
        return null;
    }

    /**
     * 强制清理一个节点。
     * 当常规清理尝试失败后调用。
     *
     * @param data 需要清理的节点对象
     */
    protected void forceClear(Q data) {
        // 具体实现根据需求决定是否需要添加逻辑
    }
}

