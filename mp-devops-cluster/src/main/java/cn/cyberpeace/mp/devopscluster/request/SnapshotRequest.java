package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotRequest {

    /**
     * 虚拟机/容器ID
     */
    @ApiModelProperty(value = "虚拟机/容器ID")
    private String instanceUuid;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
}
