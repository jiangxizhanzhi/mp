package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import lombok.Data;

import java.io.Serializable;

/**
 * Qos规则
 * @author wanggang
 */
@Data
public class QosRule implements Serializable {

    /**
     * 网络速率（kbps）
     */
    private Integer maxKbps;

    /**
     * 网络最大速率（kbps）
     */
    private Integer maxBurstKbps;

    /**
     * 指定进口出口方向
     */
    private String direction;

    /**
     * 丢包率
     */
    private Integer lossPercent;

    /**
     * 延迟时间（ms)
     */
    private Integer delayMs;

    /**
     * 延迟相关毫秒（ms）
     */
    private Integer delayCorrelationMs;
}
