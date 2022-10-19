package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackTypeVO {
    @ApiModelProperty(value = "中文名称")
    private String zhName;

    @ApiModelProperty(value = "英文名称")
    private String enName;

    @ApiModelProperty(value = "数量")
    private long count;

    @ApiModelProperty(value = "占比")
    private double percent;
}
