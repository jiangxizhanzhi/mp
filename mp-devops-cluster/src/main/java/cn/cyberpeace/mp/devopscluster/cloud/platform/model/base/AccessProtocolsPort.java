package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessProtocolsPort implements Serializable {

    @ApiModelProperty(value = "接入协议")
    private String protocol;

    @ApiModelProperty(value = "接入端口")
    private Integer port;
}
