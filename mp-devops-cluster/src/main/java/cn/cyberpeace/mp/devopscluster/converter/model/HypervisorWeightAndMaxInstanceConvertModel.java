 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorWeightAndMaxInstanceConvertModel.java.
 // File size 554.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
