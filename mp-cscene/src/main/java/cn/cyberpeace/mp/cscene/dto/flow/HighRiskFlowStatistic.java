package cn.cyberpeace.mp.cscene.dto.flow;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/18 14:38
 */
@Data
public class HighRiskFlowStatistic {

    // 场景名称
    private String name;

    // 开始时间
    private LocalDateTime startTime;

    // 结束时间
    private LocalDateTime endTime;

    // 场景实例id
    private String id;

    // 攻击TOP IP
    private List<String> fromTopIps;

    // 被攻击TOP IP
    private List<String> toTopIps;

    // 高危攻击次数
    private Integer highRiskCount;


}
