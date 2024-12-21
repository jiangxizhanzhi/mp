package cn.cyberpeace.mp.cscene.controller.request;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BorderConnectionRequest extends GuacamoleCreateConnectionData.ConnectionParams {
    @ApiModelProperty(value = "接入方式id")
    private String userId;
}
