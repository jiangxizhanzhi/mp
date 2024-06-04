package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
