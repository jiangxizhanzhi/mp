 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElementCommandExecResult.java.
 // File size 481.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName ElementCommandExecResult
 * @Description
 * @Author maojun
 * @Date 2022/7/19
 * @Version 1.0.0
 */
@Getter
@Setter
public class ElementCommandExecResult {
    @ApiModelProperty(value = "执行状态")
    private Boolean status;
    @ApiModelProperty(value = "执行结果")
    private String output;
}
