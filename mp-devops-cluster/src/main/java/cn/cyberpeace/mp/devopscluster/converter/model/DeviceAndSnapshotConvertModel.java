package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceAndSnapshotConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
