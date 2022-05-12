package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.QuickCreateParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("快照历史信息")
public class SnapshotConvertModel extends BasePageConvertModel{

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("真实快照名称")
    private String name;

    /**
     * 名称
     */
    @ApiModelProperty("用户设置的快照名称")
    private String userSnapshotName;

    /**
     * 设备ID
     */
    @ApiModelProperty("镜像UUID")
    private String deviceUuid;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("快速构建参数")
    private List<QuickCreateParam> quickCreateParams;

    public SnapshotConvertModel(String uuid, String name, String userSnapshotName, String deviceUuid, String description, LocalDateTime createdAt) {
        this.uuid = uuid;
        this.name = name;
        this.userSnapshotName = userSnapshotName;
        this.deviceUuid = deviceUuid;
        this.description = description;
        this.createdAt = createdAt;
    }
}
