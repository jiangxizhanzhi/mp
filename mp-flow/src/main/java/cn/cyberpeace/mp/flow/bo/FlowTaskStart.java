package cn.cyberpeace.mp.flow.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量任务启动对象
 * @Author qiaokang
 * @Date：2023/5/24 17:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowTaskStart {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 流量id
     */
    private String flowId;

    /**
     * 资源类型：0 脚本， 1 pcap包
     */
    private int resourceType;

    /**
     * 持续运行：0 否，1 是
     */
    private int keepRunning;

    /**
     * 通用配置
     */
    private FlowTaskCommonConf commonConf;

    /**
     * 速率无限制：0 否，1 是
     */
    private int rateUnlimited;

    /**
     * 速率配置
     */
    private FlowTaskRateConf rateConf;

    /**
     * 是否流量重放：0 否，1 是
     */
    private int replay;

    /**
     * 流配置
     */
    private FlowTaskFlowConf flowConf;

    /**
     * 攻击脚本参数
     */
    private List<FlowTaskAttackConf> attackConfs;

}
