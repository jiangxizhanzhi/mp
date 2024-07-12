 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowSimulationParams.java.
 // File size 1284.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @ClassName FlowSimulationParams
 * @Description 流量分析Request
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowSimulationParams {

    @ApiModelProperty("场景ID")
    @NotBlank
    private List<String> sceneIds;

    private List<String> nodeIds;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("源Mac地址")
    private List<String> sourceMacs;

    @ApiModelProperty("目标IP")
    private String destinationIp;

    @ApiModelProperty("目标Mac地址")
    private List<String> destinationMacs;

    @ApiModelProperty("流量大小")
    private Integer networkBytes;

    @Builder.Default
    @ApiModelProperty("随机生成攻击手法")
    private Boolean shuffleCategory = Boolean.FALSE;

    @ApiModelProperty("时间")
    private LocalDateTime dateTime;

}
