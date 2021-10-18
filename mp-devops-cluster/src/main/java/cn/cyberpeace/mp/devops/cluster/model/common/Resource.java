package cn.cyberpeace.mp.devops.cluster.model.common;

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

    /**
     * 文件地址
     */
    @ApiModelProperty("文件地址")
    private String file;

    /**
     * 平台
     */
    @ApiModelProperty("平台")
    private String platforms;

    /**
     * 临时文件ID
     */
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;

    /**
     * 安装方式
     */
    @ApiModelProperty(hidden = true)
    private Integer installMode;

    /**
     * 安装脚本
     */
    @ApiModelProperty(hidden = true)
    private String installScript;

    /**
     * 用户文件名
     */
    @ApiModelProperty("用户文件名")
    private String userFilename;
}
