package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtEntityAccess implements Serializable {

    
    @ApiModelProperty("接入类型")
    private String accessType;

    
    @ApiModelProperty("交换机端口")
    private String switchPort;

    
    @ApiModelProperty("交换机名称")
    private String switchName;

    
    @ApiModelProperty("实体端口名")
    private String portName;

    
    @ApiModelProperty("IP地址")
    private String ip;

    
    @ApiModelProperty("IMAC地址")
    private String mac;

    
    @ApiModelProperty("管理协议列表")
    private List<AccessProtocol> accessProtocols;
}
