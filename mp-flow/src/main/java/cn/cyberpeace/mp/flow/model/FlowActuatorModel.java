package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_actuator")
public class FlowActuatorModel extends BaseModel {

    /**
     * 执行器名称
     */
    @ApiModelProperty(value = "执行器名称")
    @DefaultStr
    private String title;

    /**
     * 执行器ip地址
     */
    @ApiModelProperty(value = "执行器ip地址")
    @NotBlank(message = "ip can not be empty")
    private String ip;

    /**
     * 网络id
     */
    @ApiModelProperty(value = "网络id")
    @DefaultStr
    private String networkId;

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @DefaultStr
    private String instanceId;

    /**
     * 服务端ip
     */
    @ApiModelProperty(value = "服务端ip")
    @DefaultStr
    private String serverIp;

    /**
     * trex服务端mac
     */
    @ApiModelProperty(value = "trex服务端mac")
    @DefaultStr
    private String serverMac;

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
     * trex客户端mac
     */
    @ApiModelProperty(value = "trex客户端mac")
    @DefaultStr
    private String clientMac;

    /**
     * 客户端网关
     */
    @ApiModelProperty(value = "客户端网关")
    @DefaultStr
    private String clientGateway;


}
