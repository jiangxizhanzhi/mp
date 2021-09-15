package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("虚拟机信息")
public class ExtVm {

    @ApiModelProperty("flavor信息")
    private String flavor;

    /**
     * 支持初始化
     */
    @ApiModelProperty("支持初始化")
    private Boolean initSupport;

    /**
     * 支持快速创建
     */
    @ApiModelProperty("支持快速创建")
    private Boolean quickCreateSupport;

    /**
     * 快速创建参数
     */
    @ApiModelProperty("快速创建参数")
    private String quickCreateParams;

    /**
     * 接入方式
     */
    @ApiModelProperty(hidden = true)
    private String accessProtocols;

    /**
     * 额外信息
     */
    @ApiModelProperty("扩展信息")
    private String extInfo;
}
