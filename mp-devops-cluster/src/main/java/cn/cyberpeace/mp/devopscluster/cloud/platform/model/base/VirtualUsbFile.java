package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("虚拟USB存储文件对象")
public class VirtualUsbFile extends BaseModel {

    
    @ApiModelProperty("文件名称")
    private String fileName;

    
    @ApiModelProperty("文件大小")
    private String fileSize;

    
    @ApiModelProperty("时间")
    private String fileDate;
}
