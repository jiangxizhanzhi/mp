package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackTargetListVO {
    @ApiModelProperty(value = "攻击总次数")
    private long count;

    @ApiModelProperty(value = "设备攻击次数列表")
    private List<AttackTargetVO> attackTargets;
}
