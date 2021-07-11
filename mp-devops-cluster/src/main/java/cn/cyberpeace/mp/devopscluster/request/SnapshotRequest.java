package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotRequest {

    
    @ApiModelProperty(value = "镜像ID")
    private String uuid;

    
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    
    @ApiModelProperty(value = "镜像名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;
}
