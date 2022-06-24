package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/6/23
 */
@Service
public class CsceneLogService {
    @Resource
    private CsceneMapper csceneMapper;
    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;
    @Resource
    private SceneElementMapper sceneElementMapper;
    @Resource
    private FlowInfoService flowInfoService;

    public LogDetail getLogDesc(String csceneId, boolean config) {
        String target = this.getTarget(csceneId, config);
        Map<String, String> data = new HashMap<>();
        data.put("id", csceneId);
        String content = JsonCommon.stringify(data);
        return new LogDetail(target, content);
    }

    public LogDetail getLogDesc(String csceneId, String nodeId) {
        String target = this.getTarget(csceneId, nodeId);
        Map<String, String> data = new HashMap<>();
        data.put("id", csceneId);
        data.put("nodeId", nodeId);
        String content = JsonCommon.stringify(data);
        return new LogDetail(target, content);
    }

    public LogDetail getLogDesc(String csceneId, String nodeId, String flowId) {
        String target = this.getTarget(csceneId, nodeId);
        Map<String, String> data = new HashMap<>();
        data.put("id", csceneId);
        data.put("nodeId", nodeId);
        data.put("flowId", flowId);
        String content = JsonCommon.stringify(data);
        return new LogDetail(target, content);
    }

    private String getTarget(String csceneId, boolean config) {
        if (config) {
            var model = csceneMapper.get(csceneId);
            if (model != null) {
                return model.getName();
            }
            return StringUtils.EMPTY;
        } else {
            var model = csceneInstanceMapper.get(csceneId);
            if (model != null) {
                return model.getName();
            }
            return StringUtils.EMPTY;
        }
    }

    private String getTarget(String csceneId, String nodeId) {
        var cscene = csceneInstanceMapper.get(csceneId);
        if (cscene == null) {
            return StringUtils.EMPTY;
        }
        var dbs = sceneElementMapper.list(new HumanCondition()
                .filterEqual(SceneElementModel::getScene, cscene.getScene())
                .filterEqual(SceneElementModel::getNodeId, nodeId)
        );
        if (CollectionUtils.isEmpty(dbs)) {
            return cscene.getName();
        } else {
            return cscene.getName() + "-" + dbs.get(0).getName();
        }
    }

    private String getTarget(String csceneId, String nodeId, String flowId) {
        String name = getTarget(csceneId, nodeId);
        var flow = flowInfoService.get(flowId);
        if (flow == null || flow.getSource() == null || flow.getSource().getObj() == null) {
            return name;
        } else {
            return name + "-" + flow.getSource().getObj().getTitle();
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
}
