package cn.cyberpeace.mp.flow.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CsceneFlowTaskResponse {
    private String taskId;
    private String csceneId;
    private String csceneNodeId;
    private String flowId;
    private String name;
    private Integer category;
    private Integer interactiveType;
    private List<ScriptParamModel> scriptParamModels;
}
