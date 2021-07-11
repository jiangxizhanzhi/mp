package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ActivationStatus {

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

}
