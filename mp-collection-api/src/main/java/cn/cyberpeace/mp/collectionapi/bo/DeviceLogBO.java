package cn.cyberpeace.mp.collectionapi.bo;

import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceLogBO extends DeviceLog {
    @ApiModelProperty(value = "原始日志数据")
    private Object message;
}
