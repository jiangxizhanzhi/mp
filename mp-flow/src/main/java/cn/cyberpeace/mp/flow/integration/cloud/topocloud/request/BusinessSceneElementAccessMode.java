package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessSceneElementAccessMode {
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;
}

