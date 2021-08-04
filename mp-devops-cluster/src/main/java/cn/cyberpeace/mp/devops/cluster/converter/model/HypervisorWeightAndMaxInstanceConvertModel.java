package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HypervisorWeightAndMaxInstanceConvertModel {

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 虚拟机数量限制
     */
    private Integer maxInstances;
}
