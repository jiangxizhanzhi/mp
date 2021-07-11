package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorStatisticsConvertModel {

    
    private Integer cpus;

    
    private Long memoryMb;

    
    private Long localGb;

    
    private Long cpusUsed;

    
    private Long memoryMbUsed;

    
    private Long localGbUsed;

    
    private Long freeRamMb;

    
    private Long freeDiskGb;

}
