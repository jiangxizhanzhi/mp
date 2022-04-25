package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流表基础信息
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
