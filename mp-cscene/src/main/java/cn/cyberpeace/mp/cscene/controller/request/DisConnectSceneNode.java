package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DisConnectSceneNode {
    @ApiModelProperty("连接的id")
    private String id;
}