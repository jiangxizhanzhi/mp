 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationStatus.java.
 // File size 356.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/10/26 14:22
 */
@Data
public class ActivationStatus {

    @ApiModelProperty(value = "是否是激活状态 1:是 0:否")
    private Integer isActived;

}
