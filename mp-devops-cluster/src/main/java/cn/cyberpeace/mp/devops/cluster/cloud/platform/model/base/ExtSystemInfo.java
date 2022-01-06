package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 扩展系统信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("系统信息")
public class ExtSystemInfo implements Serializable {

    /**
     * 操作系统
     */
    @ApiModelProperty("二级系统类型")
    private Integer parentSystemType;

    /**
     * 操作系统
     */
    @ApiModelProperty("三级系统类型")
    private Integer systemType;

    /**
     * 系统架构
     */
    @ApiModelProperty("系统架构")
    private String systemArchitecture;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    private String language;

    /**
     * 系统类型名称
     */
    @ApiModelProperty("系统类型名称")
    private String systemTypeName;

    /**
     * 系统架构名称
     */
    @ApiModelProperty("系统架构名称")
    private String systemArchitectureName;

    /**
     * 语言名称
     */
    @ApiModelProperty("语言名称")
    private String languageName;
}
