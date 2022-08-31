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
     * 镜像ID
     */
    @ApiModelProperty(value = "镜像ID")
    private String uuid;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    /**
     * 名称
     */
    @ApiModelProperty(value = "镜像名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
}
