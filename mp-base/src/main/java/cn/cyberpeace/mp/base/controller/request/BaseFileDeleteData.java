package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseFileDeleteData {
    @ApiModelProperty(value = "文件id")
    private String id;
}
