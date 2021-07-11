package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchStopFlowTaskRequest {

    
    private int action;

    
    private List<String> taskIds;
}
