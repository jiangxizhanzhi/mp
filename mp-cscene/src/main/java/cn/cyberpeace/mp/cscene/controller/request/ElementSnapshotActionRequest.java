package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ElementSnapshotActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CsceneInstanceNodeActionRequest
 */
@Data
public class ElementSnapshotActionRequest extends CsceneInstanceNodeActionRequest {
    private String snapshotId;
    @ApiModelProperty(value = "快照操作 回滚:rollback")
    private String action;
}
