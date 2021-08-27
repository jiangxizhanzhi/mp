package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
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

    /**
     * 服务端ip
     */
    @ApiModelProperty(value = "服务端ip")
    @DefaultStr
    private String serverIp;

    /**
     * 服务端mac
     */
    @ApiModelProperty(value = "服务端mac")
    @DefaultStr
    private String serverMac;

    /**
     * 服务端网口id
     */
    @ApiModelProperty(value = "服务端网口id")
    @DefaultStr
    private String serverPortId;

    /**
     * 服务端网关
     */
    @ApiModelProperty(value = "服务端网关")
    @DefaultStr
    private String serverGateway;

    /**
     * 客户端ip
     */
    @ApiModelProperty(value = "客户端ip")
    @DefaultStr
    private String clientIp;

    /**
     * 客户端mac
     */
    @ApiModelProperty(value = "客户端mac")
    @DefaultStr
    private String clientMac;

    /**
     * 客户端网口id
     */
    @ApiModelProperty(value = "客户端网口id")
    @DefaultStr
    private String clientPortId;

    /**
     * 客户端网关
     */
    @ApiModelProperty(value = "客户端网关")
    @DefaultStr
    private String clientGateway;

}
