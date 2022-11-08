package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeDelSnapshotRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "快照")
    private String snapshotId;
}