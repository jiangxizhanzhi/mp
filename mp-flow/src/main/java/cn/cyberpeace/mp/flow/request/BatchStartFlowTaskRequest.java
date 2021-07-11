package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchStartFlowTaskRequest {

    
    private int action;

    
    private String time;

    
    private List<FlowTaskStart> tasks;

    
    private Integer runType;

}
