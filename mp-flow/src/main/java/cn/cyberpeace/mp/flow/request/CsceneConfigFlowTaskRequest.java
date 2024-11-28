package cn.cyberpeace.mp.flow.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsceneConfigFlowTaskRequest {
    private String id;
    private String nodeId;
    private Integer page;
    private Integer pageSize;
}
