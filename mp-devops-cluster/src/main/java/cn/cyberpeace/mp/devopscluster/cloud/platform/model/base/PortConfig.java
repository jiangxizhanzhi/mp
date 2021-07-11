package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("端口配置")
public class PortConfig implements Serializable {

    
    @ApiModelProperty("配置类型")
    private Integer portType;

    
    @ApiModelProperty("端口名称")
    private String portName;

    
    @ApiModelProperty("端口网段")
    private String portIps;

    @ApiModelProperty("接入协议")
    private List<AccessProtocol> accessProtocols;
}
