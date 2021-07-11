package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartFlowTaskRequest {

    
    private int action;

    
    private String time;

    
    private FlowTaskStart task;

}
