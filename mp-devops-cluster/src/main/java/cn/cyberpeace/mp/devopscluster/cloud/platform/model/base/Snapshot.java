package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot extends BaseCloudPlatformResult {

    @ApiModelProperty(value = "快照id")
    private String id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 真实快照名称
     */
    @ApiModelProperty(value = "快照名称")
    private String name;

    /**
     * 用户快照名称
     */
    private String userSnapshotName;

    /**
     * 设备ID
     */
    private String deviceUuid;

    /**
     * 描述
     */
    @ApiModelProperty(value = "快照描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "快照创建时间")
    private String createdAt;
    @ApiModelProperty(value = "快照状态")
    private String status;
    @ApiModelProperty(value = "cpu数量")
    private Integer cpu;
    @ApiModelProperty(value = "内存大小")
    private Integer definedMemory;
    private Integer ensuredPhysicalMemory;
    private Boolean memory;

    private String imageName;

    private ExtInfo extInfo;

    @Setter
    @Getter
    public static class ExtInfo {
        private String sceneId;
    }
}
