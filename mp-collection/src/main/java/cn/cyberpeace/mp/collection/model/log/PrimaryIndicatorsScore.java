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


    @Data
    private static class PredictiveAbility {
        @JsonProperty("威胁事件感知")
        private Integer eventFeel;

        @JsonProperty("威胁事件分析")
        private Integer eventAnalyse;
    }

    @Data
    private static class AppetiteAbility {
        @JsonProperty("应急响应")
        private Integer emergencyResponse;

        @JsonProperty("损失限制")
        private Integer lossLimit;

        @JsonProperty("生存性")
        private Integer live;
    }

    @Data
    private static class RecoveryAbility {
        @JsonProperty("业务连续性")
        private Integer businessAccessible;

    }


}
