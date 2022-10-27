package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackFlowTopRequest extends AttackAggTopRequest {
    @ApiModelProperty(value = "统计类型 0:攻击次数 1:攻击类型 2:攻击协议 3:设备类型")
    private Integer aggType;

    public static class AggType {
        private AggType() {

        }

        public static final int ATTACK_COUNT = 0;

        public static final int ATTACK_TYPE = 1;

        public static final int ATTACK_PROTOCOL = 2;

        public static final int EQUIPMENT_TYPE = 3;
    }
}
