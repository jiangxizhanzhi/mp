package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamAttackVO {
    @ApiModelProperty(value = "攻击目标")
    private List<AttackTargetVO> attackTargets;

    @ApiModelProperty(value = "攻击手法")
    private List<AttackTypeVO> attackTypes;
}
