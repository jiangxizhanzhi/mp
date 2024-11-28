package cn.cyberpeace.mp.flow.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CsceneFlowTaskRequest {
    private String id;
    private String nodeId;
    private List<FlowTaskRequest> tasks;
}
