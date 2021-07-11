package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("虚拟USB存储信息")
public class VirtualUsbStorage extends BaseModel {

    
    @ApiModelProperty("容量")
    private Integer capacity;

    
    @ApiModelProperty("已使用")
    private Integer used;

    
    @ApiModelProperty("文件列表")
    private VirtualUsbFileList files;
}
