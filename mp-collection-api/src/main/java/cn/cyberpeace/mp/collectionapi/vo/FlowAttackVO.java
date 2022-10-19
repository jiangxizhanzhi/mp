package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlowAttackVO {
    @ApiModelProperty(value = "攻击次数")
    private long attackCount;

    @ApiModelProperty(value = "流量大小")
    private long size;

    @ApiModelProperty(value = "攻击手法占比")
    private List<AttackTypeVO> attackTypes;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;
}
