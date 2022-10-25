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

    @ApiModelProperty(value = "攻击目标ip集合")
    private List<String> attackTargetIp;

    @ApiModelProperty(value = "攻击手段")
    private List<String> attackTypes;

    @ApiModelProperty(value = "流量大小")
    private long size;
}
