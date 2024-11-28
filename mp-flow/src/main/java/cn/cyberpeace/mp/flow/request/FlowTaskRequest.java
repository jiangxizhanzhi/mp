package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.response.ScriptParamModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlowTaskRequest {
    private String flowId;
    private String name;
    private Integer category;
    private Integer interactiveType;
    private List<ScriptParamModel> scriptParamModels;
}
