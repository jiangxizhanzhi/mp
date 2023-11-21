package cn.cyberpeace.mp.collection.model.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName PrimaryIndicatorsScore
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/21 10:02
 **/
@Data
public class PrimaryIndicatorsScore {

    @JsonProperty("预测能力")
    private PredictiveAbility predictiveAbility;

    @JsonProperty("承受能力")
    private AppetiteAbility appetiteAbility;

    @JsonProperty("恢复能力")
    private RecoveryAbility recoveryAbility;

    @JsonProperty("适应能力")
    private AdaptiveAbility adaptiveAbility;


    @Data
    public static class PredictiveAbility {
        @JsonProperty("威胁事件感知")
        private Integer eventFeel;

        @JsonProperty("威胁事件分析")
        private Integer eventAnalyse;
    }

    @Data
    public static class AppetiteAbility {
        @JsonProperty("应急响应")
        private Integer emergencyResponse;

        @JsonProperty("损失限制")
        private Integer lossLimit;

        @JsonProperty("生存性")
        private Integer live;
    }

    @Data
    public static class RecoveryAbility {
        @JsonProperty("业务连续性")
        private Integer businessAccessible;

        @JsonProperty("错误恢复时间")
        private Integer errorRecoverTime;

        @JsonProperty("功能异构冗余度")
        private Integer functionDuplicate;

    }

    @Data
    public static class AdaptiveAbility {
        @JsonProperty("主动防御")
        private Integer activeDefence;

        @JsonProperty("基于目标环境的重构")
        private Integer refactor;
    }


}
