package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("实体USB对象")
public class EntityUsb {

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("主机名")
    private String host;
}
