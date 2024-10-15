package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateNode {
    @ApiModelProperty(value = "场景id")
    private String sceneId;
    @ApiModelProperty("节点id,当前场景内唯一")
    private String id;
    @ApiModelProperty(value = "节点名称")
    private String name;
    @ApiModelProperty(value = "节点类型")
    private String imageType;
    @ApiModelProperty(value = "镜像名称")
    private String image;
    @ApiModelProperty(value = "虚拟机/容器镜像id")
    private String imageUuid;
    @ApiModelProperty(value = "cpu数量")
    private Integer cpu;
    @ApiModelProperty(value = "内存大小")
    private Integer ram;
    @ApiModelProperty(value = "磁盘大小")
    private Integer disk;
    @ApiModelProperty("节点对应的云资源资源id")
    private String resourceId;
    @ApiModelProperty("端口信息")
    private List<CreateNodePort> ports;
}