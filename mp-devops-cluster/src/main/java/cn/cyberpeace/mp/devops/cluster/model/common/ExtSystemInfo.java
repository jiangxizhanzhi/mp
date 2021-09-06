package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("系统信息")
public class ExtSystemInfo {

    /**
     * 操作系统
     */
    @ApiModelProperty("系统类型")
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
}
