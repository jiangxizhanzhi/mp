package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.model.DeviceToolModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeviceToolVO extends Device {

    @ApiModelProperty(value = "工具列表")
    private List<DeviceToolModel> tools;
}
