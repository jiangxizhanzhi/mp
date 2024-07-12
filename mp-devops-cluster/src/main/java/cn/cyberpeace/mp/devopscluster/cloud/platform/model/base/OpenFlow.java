 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlow.java.
 // File size 976.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OpenFlow
 * @Description 流表基础信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class OpenFlow {

    /**
     * 动作
     */
    private String actions;

    /**
     * 匹配条件
     */
    private String matchCondition;

    /**
     * cookie
     */
    private String cookie;

    /**
     * 持续时间
     */
    private String duration;

    /**
     * 表
     */
    private String table;

    /**
     * 包数
     */
    private String nPackets;

    /**
     * 字节数
     */
    private String nBytes;

    /**
     * 空闲时间
     */
    private String idleAge;

    /**
     * 网桥
     */
    private String ovsName;

    /**
     * 节点
     */
    private String hostname;

}
