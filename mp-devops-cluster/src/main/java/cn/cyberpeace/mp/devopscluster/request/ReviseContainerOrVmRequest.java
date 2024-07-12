 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ReviseContainerOrVmRequest.java.
 // File size 560.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ReviseContainerOrVmRequest
 * @Description
 * @Author zhanglin
 * @Date 2022/10/12
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviseContainerOrVmRequest {
    /**
     * 实例对应的云平台的id
     */
    @ApiModelProperty(value = "实例对应的云平台的id")
    private String id;
}
