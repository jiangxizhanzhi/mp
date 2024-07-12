 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualRealSwitchBO.java.
 // File size 738.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VirtualRealSwitchBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualRealSwitchBO {

    /**
     * 名称
     */
    private String name;

    /**
     * 管理IP
     */
    private String managementIp;

    /**
     * 节点管理
     */
    private String state;

    /**
     * 端口总数
     */
    private Integer ports;

    /**
     * 已使用端口数
     */
    private Integer usedPorts;
}
