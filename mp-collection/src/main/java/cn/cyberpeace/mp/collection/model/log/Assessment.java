package cn.cyberpeace.mp.collection.model.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName Assessment
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 18:59
 **/
@Data
public class Assessment {

    @Field(name = "cyber_resilience", type = FieldType.Integer)
    private Integer cyberResilience;

    @Field(name = "empowerment_level", type = FieldType.Keyword)
    private String empowermentLevel;

    @Field(type = FieldType.Object)
    private CyberResiliencyCapability cyberResiliencyCapability;

    @Field(type = FieldType.Object)
    private PrimaryIndicatorsScore primaryIndicatorsScore;


    @Field(name = "average_threat_isolation_time", type = FieldType.Keyword)
    @JsonProperty("平均威胁隔离时间")
    private String averageThreatIsolationTime;

    @Field(name = "average_system_repair_time", type = FieldType.Keyword)
    @JsonProperty("平均系统修复时间")
    private String averageSystemRepairTime;

    @Field(name = "system_unavailable_time", type = FieldType.Integer)
    @JsonProperty("服务不可用次数")
    private Integer systemUnavailableTime;
}
