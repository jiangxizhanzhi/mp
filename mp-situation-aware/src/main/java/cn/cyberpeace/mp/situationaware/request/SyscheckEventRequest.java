package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SyscheckEventRequest extends BaseEventRequest{
    @ApiModelProperty(value = "文件或注册表路径")
    private String path;
}
