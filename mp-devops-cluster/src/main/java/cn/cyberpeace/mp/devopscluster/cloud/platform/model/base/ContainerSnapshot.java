package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContainerSnapshot {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "快照名称")
    private String name;
    @ApiModelProperty(value = "容器id")
    private String containerId;
    @ApiModelProperty(value = "快照id")
    private String imageId;
    @ApiModelProperty(value = "创建时间")
    private String createAt;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态")
    private String status;
}
