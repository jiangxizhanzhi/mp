package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName HypervisorWeightAndMaxInstanceConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
