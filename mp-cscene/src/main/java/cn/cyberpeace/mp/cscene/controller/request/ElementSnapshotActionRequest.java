package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElementSnapshotActionRequest extends CsceneInstanceNodeActionRequest {
    private String snapshotId;
    @ApiModelProperty(value = "快照操作 回滚:rollback")
    private String action;
}
