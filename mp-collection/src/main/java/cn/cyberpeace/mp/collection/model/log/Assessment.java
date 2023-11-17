package cn.cyberpeace.mp.collection.model.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName Assessment
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 18:59
 **/
@Data
public class Assessment {
    private Integer cyberResilience;

    private String empowermentLevel;

    private CyberResiliencyCapability cyberResiliencyCapability;

    @JsonProperty("平均威胁隔离时间")
    private String averageThreatIsolationTime;

    @JsonProperty("平均系统修复时间")
    private String averageSystemRepairTime;
}
