 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowPageDTO.java.
 // File size 719.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @ClassName OpenFlowPageDTO
 * @Description 流表操作请求信息
 * @Author qiaokang
 * @Date 2021/7/9 14:43
 * @Version 1.0.0
 * @see BasePageDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageDTO extends BasePageDTO {

    @ApiModelProperty(value = "执行动作", hidden = true)
    @NotNull(message = "执行动作不能为空")
    @Builder.Default
    private String method = "list";

}
