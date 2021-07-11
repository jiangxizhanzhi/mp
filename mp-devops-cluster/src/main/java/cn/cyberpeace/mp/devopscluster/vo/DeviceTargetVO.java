package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTargetVO {

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("图标")
    private String logo;

    
    @ApiModelProperty("角色")
    private Integer role;

    
    @ApiModelProperty("一级分类")
    private Integer category;

    
    @ApiModelProperty("二级分类")
    private Integer secondCategory;

    
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("镜像类型")
    private String imageType;
}
