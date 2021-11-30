package cn.cyberpeace.mp.cscene.dto.element;

import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点基本信息")
public class ElementMonitorBasicInfo {

    @ApiModelProperty(value = "节点基本信息-实例名称")
    private String name;

    @ApiModelProperty(value = "节点基本信息-IP地址")
    private List<String> ips;

    @ApiModelProperty(value = "节点基本信息-操作系统")
    private String systemType;

    @ApiModelProperty(value = "节点基本信息-镜像名称")
    private String imageName;

    @ApiModelProperty(value = "节点基本信息-物理节点")
    private String host;

    @ApiModelProperty(value = "节点基本信息-实例类型")
    // 对应 BaseCloudPluginSceneElementModel.VirtualType 中的枚举？
    private Integer virtualType;

    @ApiModelProperty(value = "节点资源配置信息")
    private HostState hostState;

    @ApiModelProperty(value = "节点资源配置信息")
    private List<SceneElementPortBO> ports;

}
