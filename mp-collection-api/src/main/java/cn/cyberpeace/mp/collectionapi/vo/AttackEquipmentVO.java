package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackEquipmentVO {
    @ApiModelProperty(value = "设备类型")
    private String type;

    @ApiModelProperty(value = "占比")
    private Double percent;
}
