package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseDeleteData {
    @ApiModelProperty(value = "数据id")
    private String id;
}
