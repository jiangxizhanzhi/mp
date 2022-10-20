package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackTargetListVO {
    @ApiModelProperty(value = "攻击总次数")
    private long count;

    @ApiModelProperty(value = "设备攻击次数列表")
    private List<AttackTargetVO> attackTargets;
    
    @ApiModelProperty(value = "设备被攻击手法top5")
    private List<AttackTypeVO> attackTypes;
    
    @ApiModelProperty(value = "设备类型")
    private Integer useType;
}
