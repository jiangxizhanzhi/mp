package cn.cyberpeace.mp.collection.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SyscheckEventRequest extends BaseEventRequest{
    @ApiModelProperty(value = "文件或注册表路径")
    private String path;
}
