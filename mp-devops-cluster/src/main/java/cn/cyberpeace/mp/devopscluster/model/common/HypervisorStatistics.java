package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorStatistics implements Serializable {

    
    private Integer count;

    
    private Integer vcpus;

    
    private Long memoryMb;

    
    private Long localGb;

    
    private Long vcpusUsed;

    
    private Long memoryMbUsed;

    
    private Long localGbUsed;

    
    private Long freeRamMb;

    
    private Long freeDiskGb;

    
    private Long currentWorkload;

    
    private Integer runningVms;

    
    private Long diskAvailableLeast;
}

