package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotMergeRequest {

    /**
     * 镜像ID
     */
    @ApiModelProperty(value = "镜像ID",required = true)
    @NotBlank
    private String uuid;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型",required = true)
    @NotBlank
    private String imageType;

    /**
     * 最新一条的快照名称
     */
    @ApiModelProperty(value = "最新一条的快照名称",required = true)
    @NotBlank
    private String snapshotName;

    /**
     * 新的快照名称
     */
    @ApiModelProperty("新的快照名称")
    private String newName;
}
