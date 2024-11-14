package cn.cyberpeace.mp.flow.integration.cloud.topocloud.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessSceneCloudPluginElement {
    @ApiModelProperty(value = "角色id,网络：1")
    private Integer role;
    @ApiModelProperty(value = "节点虚拟类型，虚拟机:vm , 容器：container,其他: simple")
    private String imageType;
    @ApiModelProperty(value = "靶标id")
    private String imageUuid;
    @ApiModelProperty(value = "靶标名称")
    private String image;
    @ApiModelProperty(value = "cidr", required = false)
    private String cidr;
    @ApiModelProperty(value = "网关ip", required = false)
    private String gateway;
    @ApiModelProperty(value = "是否外部节点（云平台上已存在资源，如果为true，则resourceId必填）", required = false)
    private Boolean external;
    @ApiModelProperty(value = "云平台资源id", required = false)
    private String resourceId;
}
