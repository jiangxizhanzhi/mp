package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttackProtocolVO {
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "占比")
    private Double percent;
}
