package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAndSnapshotConvertModel{

    
    @ApiModelProperty("镜像UUID")
    private String deviceUuid;

    
    @ApiModelProperty("镜像名称")
    private String deviceName;

    
    @ApiModelProperty("快照列表")
    private List<SnapshotConvertModel> items;

}
