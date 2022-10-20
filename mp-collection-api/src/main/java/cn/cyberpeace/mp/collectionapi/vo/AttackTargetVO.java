package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackTargetVO {
    @ApiModelProperty(value = "设备类型")
    private String name;

    @ApiModelProperty(value = "攻击次数")
    private long count;
    
    @ApiModelProperty(value = "设备类型")
    private Integer useType;
}
