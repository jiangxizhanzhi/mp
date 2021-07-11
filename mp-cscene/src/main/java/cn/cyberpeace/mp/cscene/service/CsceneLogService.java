package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class CsceneLogService {
    private static final String DESC_SCENE = "cscene.oplog.desc.scene";
    private static final String DESC_SCENE_CONFIG = "cscene.oplog.desc.scene_config";
    private static final String DESC_SCENE_ELEMENT = "cscene.oplog.desc.scene_element";
    private static final String DESC_SCENE_ELEMENT_FLOW = "cscene.oplog.desc.scene_element_flow";
    private static final String BATCH_SCENES = "cscene.oplog.desc.batch_scenes";
    private static final String BATCH_SCENE_CONFIGS = "cscene.oplog.desc.batch_scene_configs";

    @Resource
    private CsceneMapper csceneMapper;
    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;
    @Resource
    private SceneElementMapper sceneElementMapper;
    @Resource
    private FlowInfoService flowInfoService;

    public LogDetail getLogDetail(String csceneId, boolean config) {
        List<String> target = this.getTarget(csceneId, config);
        LogContent logContent = new LogContent();
        logContent.setId(csceneId);
        String content = JsonCommon.stringify(logContent);
        return new LogDetail(StringUtils.join(target, ","), content);
    }

    public LogDetail getLogDetail(List<String> csceneIds, boolean config) {
        LogContent logContent = new LogContent();
        logContent.setIds(csceneIds);
        String content = JSON.toJSONString(logContent);
        if (config) {
            return new LogDetail(MessageCommon.get(BATCH_SCENE_CONFIGS), content);
        } else {
            return new LogDetail(MessageCommon.get(BATCH_SCENES), content);
        }
    }

    public LogDetail getLogDetail(String csceneId, String nodeId) {
        List<String> target = this.getTarget(csceneId, nodeId);
        LogContent logContent = new LogContent();
        logContent.setId(csceneId);
        logContent.setNodeId(nodeId);
        String content = JSON.toJSONString(logContent);
        return new LogDetail(StringUtils.join(target, ","), content);
    }

    public LogDetail getLogDetail(String csceneId, String nodeId, String flowId) {
        List<String> target = this.getTarget(csceneId, nodeId, flowId);
        LogContent logContent = new LogContent();
        logContent.setId(csceneId);
        logContent.setNodeId(nodeId);
        if (flowId != null) {
            logContent.setFlowId(flowId);
        }
        String content = JSON.toJSONString(logContent);
        return new LogDetail(StringUtils.join(target, ","), content);
    }

    private List<String> getTarget(String csceneId, boolean config) {
        if (config) {
            var model = csceneMapper.get(csceneId);
            if (model != null) {
                return Arrays.asList(MessageCommon.get(DESC_SCENE_CONFIG, model.getName()));
            }
            return new ArrayList<>();
        } else {
            var model = csceneInstanceMapper.get(csceneId);
            if (model != null) {
                return Arrays.asList(MessageCommon.get(DESC_SCENE, model.getName()));
            }
            return new ArrayList<>();
        }
    }

    private List<String> getTarget(String csceneId, String nodeId) {
        var cscene = csceneInstanceMapper.get(csceneId);
        if (cscene == null) {
            return new ArrayList<>();
        }
        var dbs = sceneElementMapper.list(new HumanCondition()
                .filterEqual(SceneElementModel::getScene, cscene.getScene())
                .filterEqual(SceneElementModel::getNodeId, nodeId)
        );
        if (CollectionUtils.isEmpty(dbs)) {
            return Arrays.asList(MessageCommon.get(DESC_SCENE, cscene.getName()));
        } else {
            return Arrays.asList(MessageCommon.get(DESC_SCENE, cscene.getName()), MessageCommon.get(DESC_SCENE_ELEMENT, dbs.get(0).getName()));
        }
    }

    private List<String> getTarget(String csceneId, String nodeId, String flowId) {
        List<String> targets = getTarget(csceneId, nodeId);
        if (StringUtils.isEmpty(flowId)) {
            return targets;
        }
        var flow = flowInfoService.get(flowId);
        if (flow == null || flow.getSource() == null || flow.getSource().getObj() == null) {
            return targets;
        } else {
            List<String> result = new ArrayList<>();
            result.addAll(targets);
            result.add(MessageCommon.get(DESC_SCENE_ELEMENT_FLOW, flow.getSource().getObj().getTitle()));
            return result;
        }
    }

    @Data
    public static class LogDetail {
        private String target;
        private String content;

        public LogDetail(String target, String content) {
            this.target = target;
            this.content = content;
        }
    }

    @Data
    private static class LogContent {
        private List<String> ids;
        private String id;
        private String nodeId;
        private String flowId;
    }
}
