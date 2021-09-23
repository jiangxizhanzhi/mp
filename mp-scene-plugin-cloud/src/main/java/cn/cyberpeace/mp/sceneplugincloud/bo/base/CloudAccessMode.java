package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CloudAccessMode extends AccessMode {

    @ApiModelProperty(value = "基础协议tcp/udp")
    private String baseProtocol;

    @ApiModelProperty(value = "开启代理")
    private Boolean proxy;

    @ApiModelProperty(value = "接入ip")
    private String accessIp;

    @ApiModelProperty(value = "接入端口")
    private Integer accessPort;

    @ApiModelProperty(value = "连接url")
    private String connectionUrl;

    @ApiModelProperty(value = "连接id")
    @JsonIgnore
    private String connectionId;

    @ApiModelProperty(value = "代理id")
    @JsonIgnore
    private String proxyId;

}
