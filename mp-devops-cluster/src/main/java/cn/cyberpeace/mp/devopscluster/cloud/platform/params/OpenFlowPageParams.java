 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowPageParams.java.
 // File size 1000.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @ClassName OpenFlowPageParams
 * @Description 流表操作请求信息
 * @Author qiaokang
 * @Date 2021/7/9 14:43
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageParams{

    @ApiModelProperty(value = "执行动作", required = true)
    @NotNull(message = "执行动作不能为空")
    private String method;

    /**
     * 每页条数
     */
    @Min(1)
    @ApiModelProperty(value = "每页条数",hidden = true)
    private Integer limit;

    /**
     * 页码，从1开始
     */
    @Min(1)
    @ApiModelProperty(value = "页码",hidden = true)
    private Integer offset;
}
