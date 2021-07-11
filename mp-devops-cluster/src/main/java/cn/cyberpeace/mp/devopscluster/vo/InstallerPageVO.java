package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.StartUpParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "服务分页返回信息")
public class InstallerPageVO extends BaseVO {

    
    @ApiModelProperty("UUID")
    private String uuid;

    
    @ApiModelProperty("名称")
    private String name;

    
    @ApiModelProperty("图标地址")
    private String logo;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("类型")
    private String categories;

    
    @ApiModelProperty("服务类型")
    private Integer deployType;

    
    @ApiModelProperty("启动参数")
    private List<StartUpParameter> startUpParameterList;

    
    @ApiModelProperty(value = "文件地址")
    private String file;
}
