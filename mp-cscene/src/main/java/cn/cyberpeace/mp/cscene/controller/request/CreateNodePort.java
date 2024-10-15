package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateNodePort {
    @ApiModelProperty("端口id,当前系统唯一")
    private String eid;
    @ApiModelProperty("端口id,当前系统唯一")
    private String id;
    @ApiModelProperty(value = "ip地址")
    private String ip;
    @ApiModelProperty(value = "接入的网络id")
    private String networkId;
    @ApiModelProperty(value = "接入方式")
    private List<CreateNodePortAccessMode> accessMode;
}