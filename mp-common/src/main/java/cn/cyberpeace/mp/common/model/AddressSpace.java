package cn.cyberpeace.mp.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "地址空间")
public class AddressSpace {

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "地址大小")
    private Integer addressSize;
}

