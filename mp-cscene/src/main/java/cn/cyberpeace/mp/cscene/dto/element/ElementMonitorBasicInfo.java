package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.sceneplugincloud.bo.AccessUserBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data

@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点基本信息")
public class ElementMonitorBasicInfo {


    @ApiModelProperty(value = "场景ID")
    private String sceneId;

    @ApiModelProperty(value = "节点ID")
    private String id;

    @ApiModelProperty(value = "EID")
    private String eid;

    @ApiModelProperty(value = "节点基本信息-实例名称")
    private String name;

    @ApiModelProperty(value = "节点基本信息-2d图标地址")
    private String icon2;

    @ApiModelProperty(value = "节点基本信息-操作系统ID")
    private String systemType;

    @ApiModelProperty(value = "节点基本信息-操作系统名称")
    private String systemTypeName;

    @ApiModelProperty(value = "设备类型")
    private Integer category;

    @ApiModelProperty(value = "事件")
    private Integer event;

    @ApiModelProperty(value = "事件状态")
    private Integer eventStatus;

    @ApiModelProperty(value = "节点基本信息-镜像名称")
    private String imageName;

    @ApiModelProperty(value = "节点基本信息-镜像类型")
    private String imageType;

    @ApiModelProperty(value = "节点基本信息-角色")
    private Integer role;

    @ApiModelProperty(value = "节点基本信息-是否内置靶标")
    private Boolean isBuildIn;

    @ApiModelProperty(value = "节点基本信息-物理节点")
    private String host;

    @ApiModelProperty(value = "节点基本信息-实例类型  1: 交换机, 2: 路由器, 3: 防火墙, 4: 虚拟机, 5: 容器, 6: 实体设备")
    
    private Integer virtualType;

    @ApiModelProperty(value = "节点基本信息-实例类型  1: 交换机, 2: 路由器, 3: 防火墙, 4: 终端")
    private Integer type;

    @ApiModelProperty(value = "是否外部引用资源")
    private Boolean external;

    @ApiModelProperty(value = "节点资源配置信息")
    private HostState hostState;

    @ApiModelProperty(value = "端口")
    private List<ElementPort> ports;

    @ApiModelProperty(value = "节点实时信息")
    private ElementRTInfo rtInfo;

    @ApiModelProperty(value = "接入用户集合")
    private List<AccessUserBO> accessUsers;

}
