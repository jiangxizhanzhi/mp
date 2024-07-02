package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SnapshotActionRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SnapshotActionRequest {
    @ApiModelProperty(value = "实例id")
    private String instanceId;

    @ApiModelProperty(value = "快照id")
    private String snapshotId;

    @ApiModelProperty(value = "快照操作 回滚: rollback")
    private String action;

    @ApiModelProperty(value = "镜像类型")
    private String imageType;
}
