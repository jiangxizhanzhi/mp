package cn.cyberpeace.mp.collection.model.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName CyberResiliencyCapability
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 19:00
 **/
@Data
public class CyberResiliencyCapability {
    @JsonProperty("预测能力")
    private Integer predictiveAbility;

    @JsonProperty("承受能力")
    private Integer appetiteAbility;

    @JsonProperty("恢复能力")
    private Integer recoveryAbility;

    @JsonProperty("适应能力")
    private Integer adaptiveAbility;
}
