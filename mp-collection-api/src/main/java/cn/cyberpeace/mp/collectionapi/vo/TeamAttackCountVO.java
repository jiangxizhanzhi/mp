package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamAttackCountVO {
    @ApiModelProperty(value = "队伍名称")
    private String team;

    @ApiModelProperty(value = "攻击次数")
    private long count;

    @ApiModelProperty(value = "攻击目标数量")
    private Integer attackTarget;

    @ApiModelProperty(value = "攻击手段")
    private List<AttackTypeVO> attackTypes;

    @ApiModelProperty(value = "流量大小")
    private long size;
}
