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

    
    @ApiModelProperty(value = "靶标id", required = true)
    @NotBlank
    private String deviceUuid;

    
    @ApiModelProperty(value = "快照id", required = true)
    @NotBlank
    private String snapshotUuid;

    
    @ApiModelProperty(value = "快照名称", required = true)
    @NotBlank
    private String snapshotName;

}
