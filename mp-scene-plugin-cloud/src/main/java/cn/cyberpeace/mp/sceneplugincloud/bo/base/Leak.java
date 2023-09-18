package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Leak {
    @ApiModelProperty(value = "漏洞名称")
    private String name;
    @ApiModelProperty(value = "严重性")
    private String severity;
    @ApiModelProperty(value = "应用版本")
    private String version;
}
