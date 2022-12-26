package cn.cyberpeace.mp.collectionapi.vo;

import cn.cyberpeace.mp.collection.bo.AttackTypeBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AggAttackFlowVO {
    @ApiModelProperty(value = "聚合标识,跟随请求")
    private String name;

    private FlowSize flowSize;

    private AttackCount attackCount;

    private Map<String, AttackTypeBO> attackTypeBOMap;

    private List<AttackTypeBO> attackTypeBOS ;

    @Data
    public static class FlowSize {
        @ApiModelProperty(value = "流量大小")
        private long size;

        private Double percent;
    }

    @Data
    public static class AttackCount {
        @ApiModelProperty(value = "攻击次数")
        private long attackCount;

        private Double percent;
    }
}
