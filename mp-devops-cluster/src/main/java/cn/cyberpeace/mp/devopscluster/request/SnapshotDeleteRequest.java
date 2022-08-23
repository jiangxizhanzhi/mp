package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotDeleteRequest {

    /**
     * 靶标id
     */
    @ApiModelProperty(value = "靶标id", required = true)
    @NotBlank
    private String deviceUuid;

    /**
     * 快照id
     */
    @ApiModelProperty(value = "快照id", required = true)
    @NotBlank
    private String snapshotUuid;

    /**
     * 快照名称
     */
    @ApiModelProperty(value = "快照名称", required = true)
    @NotBlank
    private String snapshotName;

}
