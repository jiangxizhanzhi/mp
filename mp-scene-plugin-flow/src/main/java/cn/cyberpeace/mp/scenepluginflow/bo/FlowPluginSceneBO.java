package cn.cyberpeace.mp.scenepluginflow.bo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scenepluginflow.bo.base.BaseFlowPluginSceneBO;
import cn.cyberpeace.mp.scenepluginflow.model.FlowPluginSceneModel;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class FlowPluginSceneBO extends BaseFlowPluginSceneBO<FlowPluginSceneModel, FlowPluginSceneBO, SceneBO> {

    @ApiModelProperty(value = "流量发生器id")
    private List<Executor> executor;

    public String getExecutorId(String nodeId) {
        for (Executor executor : executor) {
            if (executor.getNodeIds().contains(nodeId)) {
                return executor.getExecutorId();
            }
        }
        return null;
    }

    public void setExecutorId(String nodeId, String executorId){
        for (Executor executor : executor) {
            if (executor.getNodeIds().contains(nodeId)) {
                executor.setExecutorId(executorId);
            }
        }
    }

    @Override
    public FlowPluginSceneModel createModel() {
        return new FlowPluginSceneModel();
    }

    @Override
    public void fromModel(FlowPluginSceneModel obj, String... ignoreFields) {
        super.fromModel(obj, ignoreFields);
        if (StringUtils.isNotEmpty(obj.getExecutor())) {
            executor = JsonCommon.parse(obj.getExecutor(), new TypeReference<List<Executor>>() {
            });
        } else {
            executor = new ArrayList<>();
        }
    }

    @Override
    public FlowPluginSceneModel toModel(String... ignoreFields) {
        FlowPluginSceneModel obj = super.toModel(ignoreFields);
        if (executor != null) {
            obj.setExecutor(JsonCommon.stringify(executor));
        }
        return obj;
    }

    @Getter
    @Setter
    public static class Executor {
        private String executorId;
        private Set<String> nodeIds;
    }
}
