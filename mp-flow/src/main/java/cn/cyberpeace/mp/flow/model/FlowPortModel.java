package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_flow_port")
public class FlowPortModel extends BaseModel {

    
    @ApiModelProperty(value = "服务端ip")
    @DefaultStr
    private String serverIp;

    
    @ApiModelProperty(value = "服务端mac")
    @DefaultStr
    private String serverMac;

    
    @ApiModelProperty(value = "服务端网口id")
    @DefaultStr
    private String serverPortId;

    
    @ApiModelProperty(value = "服务端网关")
    @DefaultStr
    private String serverGateway;

    
    @ApiModelProperty(value = "客户端ip")
    @DefaultStr
    private String clientIp;

    
    @ApiModelProperty(value = "客户端mac")
    @DefaultStr
    private String clientMac;

    
    @ApiModelProperty(value = "客户端网口id")
    @DefaultStr
    private String clientPortId;

    
    @ApiModelProperty(value = "客户端网关")
    @DefaultStr
    private String clientGateway;

    
    @ApiModelProperty(value = "网络id")
    @DefaultStr
    private String networkId;

    
    @ApiModelProperty(value = "流量执行器id")
    @DefaultStr
    private String actuatorId;

    
    @ApiModelProperty(value = "网口类型：0 背景流量，1攻击流量")
    @DefaultInt
    private int portType;

}
