package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateNodePortAccessMode {
    @ApiModelProperty(value = "接入模式id")
    private String id;

    @ApiModelProperty(value = "接入ip")
    private String ip;

    @ApiModelProperty(value = "接入端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "连接url")
    private String connectionUrl;

    @ApiModelProperty(value = "连接id")
    private String connectionId;

    @ApiModelProperty(value = "连接所属用户")
    private String connectionUser;
}