package cn.cyberpeace.mp.flow.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowTaskStart {

    
    private String taskId;

    
    private String flowId;

    
    private int resourceType;

    
    private int keepRunning;

    
    private FlowTaskCommonConf commonConf;

    
    private int rateUnlimited;

    
    private FlowTaskRateConf rateConf;

    
    private int replay;

    
    private FlowTaskFlowConf flowConf;

    
    private List<FlowTaskAttackConf> attackConfs;

}
