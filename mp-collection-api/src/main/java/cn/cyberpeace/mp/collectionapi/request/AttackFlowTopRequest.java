package cn.cyberpeace.mp.collectionapi.request;

import lombok.Data;

@Data
public class AttackFlowTopRequest extends AttackFlowBaseRequest {
    private Integer topSize;

    private Integer aggType;

    public static class AggType{
        public static Integer ATTACK_COUNT = 0;

        public static Integer ATTACK_TYPE = 1;

        public static Integer ATTACK_PROTOCOL = 2;

        public static Integer EQUIPMENT_TYPE = 3;
    }
}
