package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeSnapshotRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "快照名称")
    private String snapshotName;
    @ApiModelProperty(value = "快照描述")
    private String description;
}
