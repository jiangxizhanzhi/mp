package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("系统信息")
public class ExtSystemInfo implements Serializable {

    
    @ApiModelProperty("二级系统类型")
    private Integer parentSystemType;

    
    @ApiModelProperty("三级系统类型")
    private Integer systemType;

    
    @ApiModelProperty("系统架构")
    private String systemArchitecture;

    
    @ApiModelProperty("语言")
    private String language;

    
    @ApiModelProperty("系统类型名称")
    private String systemTypeName;

    
    @ApiModelProperty("系统架构名称")
    private String systemArchitectureName;

    
    @ApiModelProperty("语言名称")
    private String languageName;
}
