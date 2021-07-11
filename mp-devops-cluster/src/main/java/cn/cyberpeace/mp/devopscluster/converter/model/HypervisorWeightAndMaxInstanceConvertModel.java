package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HypervisorWeightAndMaxInstanceConvertModel {

    
    private Integer weight;

    
    private Integer maxInstances;
}
