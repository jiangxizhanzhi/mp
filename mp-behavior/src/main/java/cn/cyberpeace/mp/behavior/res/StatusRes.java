package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StatusRes implements Serializable {

    @ApiModelProperty("status")
    private Integer status;

    @ApiModelProperty("名称")
    private String name;
}
