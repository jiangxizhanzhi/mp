package cn.cyberpeace.mp.situationaware.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 聚合攻击方式
 * @author wanggang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttackWayAggregationVO extends BaseVO {

    /**
     * 聚合类型
     */
    @ApiModelProperty(value = "聚合类型")
    private String aggregationType;
}
