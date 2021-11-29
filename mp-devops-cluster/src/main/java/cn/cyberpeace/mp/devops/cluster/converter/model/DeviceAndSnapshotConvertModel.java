package cn.cyberpeace.mp.devops.cluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAndSnapshotConvertModel{

    /**
     * 设备ID
     */
    @ApiModelProperty("镜像UUID")
    private String deviceUuid;

    /**
     * 设备名称
     */
    @ApiModelProperty("镜像名称")
    private String deviceName;

    /**
     * 快照列表
     */
    @ApiModelProperty("快照列表")
    private List<SnapshotConvertModel> items;

}
