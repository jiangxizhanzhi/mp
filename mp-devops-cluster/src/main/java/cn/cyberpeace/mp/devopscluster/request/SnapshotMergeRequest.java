package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotMergeRequest {

    
    @ApiModelProperty(value = "镜像ID",required = true)
    @NotBlank
    private String uuid;

    
    @ApiModelProperty(value = "镜像类型",required = true)
    @NotBlank
    private String imageType;

    
    @ApiModelProperty(value = "最新一条的快照名称",required = true)
    @NotBlank
    private String snapshotName;

    
    @ApiModelProperty("新的快照名称")
    private String newName;
}
