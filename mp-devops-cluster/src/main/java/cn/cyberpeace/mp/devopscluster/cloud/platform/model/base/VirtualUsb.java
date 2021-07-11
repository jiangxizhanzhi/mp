package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("虚拟USB对象")
public class VirtualUsb extends BaseModel {

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("容量")
    private Integer capacity;

    
    @ApiModelProperty("类型，storage：存储")
    private String virtualUsbType;

    
    @ApiModelProperty("状态，no：待编辑 succ：已保存")
    private String virtualUsbStatus;
}
