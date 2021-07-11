package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资源信息")
public class Resource implements Serializable {

    
    @ApiModelProperty("文件地址")
    private String file;

    
    @ApiModelProperty("平台")
    private String platforms;

    
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;

    
    @ApiModelProperty(hidden = true)
    private Integer installMode;

    
    @ApiModelProperty(hidden = true)
    private String installScript;

    
    @ApiModelProperty("用户文件名")
    private String userFilename;
}
