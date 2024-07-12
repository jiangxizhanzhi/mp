 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NpcModel.java.
 // File size 632.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName NpcModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpcModel implements Serializable {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 网口IP
     */
    private String nodeIp;

    /**
     * 控制节点/计算节点
     */
    private String hostname;
}
